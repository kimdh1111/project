<template>
  <div>
    <h1>병상 통계 차트</h1>
    <canvas id="IcuInfoPie" width="300" height="100"></canvas>

    <!-- 필터 UI -->
    <div>
      <label for="monthFilter">월:</label>
      <select id="monthFilter" v-model="selectedMonth" @change="filterData">
        <option value="">전체</option>
        <option v-for="month in uniqueMonths" :key="month" :value="month">{{ month }}월</option>
      </select>
    </div>
    <!-- 부서 필터 -->
    <div>
      <label for="departmentFilter">필터:</label>
      <select id="departmentFilter" v-model="selectedDepartment">
        <option value="">전체</option>
        <option value="일반">일반</option>
        <option value="소아">소아</option>
        <option value="응급실 음압 격리 병상">응급실 음압 격리 병상</option>
        <option value="응급실 일반 격리 병상">응급실 일반 격리 병상</option>
        <option value="분만실">분만실</option>
      </select>
    </div>

    <div v-if="loading">데이터 로딩 중...</div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue';
import Chart from 'chart.js/auto';

export default {
  name: 'IcuInfoPie',
  setup() {
    const icuInfoList = ref([]); // 병상 데이터 (IcuInfo)
    const selectedMonth = ref('');
    const selectedDepartment = ref('');
    const loading = ref(true);
    let chartInstance = null; // 차트 인스턴스를 저장

    // 병상 데이터를 API에서 가져오는 함수
    const fetchIcuInfo = async () => {
      try {
        const response = await fetch('http://localhost:8695/api/icuinfo'); // API URL 수정
        const data = await response.json();
        console.log('Fetched ICU info:', data);
        icuInfoList.value = data;
      } catch (error) {
        console.error('Error fetching ICU info:', error);
      } finally {
        loading.value = false;
      }
    };

    // 부서 필터에 따라 병상 데이터 필터링하는 computed 속성
    const filteredData = computed(() => {
      let filtered = icuInfoList.value;

      // 부서 필터
      if (selectedDepartment.value) {
        filtered = filtered.filter(item =>
            item.department.toLowerCase() === selectedDepartment.value.toLowerCase()
        );
      }

      // 월 필터
      if (selectedMonth.value) {
        filtered = filtered.filter(item => {
          const dateStr = item.hvidate; // 예: "20241223141803"
          const year = dateStr.slice(0, 4);  // 예: "2024"
          const month = dateStr.slice(4, 6); // 예: "12"
          return parseInt(month) === parseInt(selectedMonth.value);
        });
      }

      return filtered;
    });

    // 병원별로 병상 데이터 합산
    const groupedData = computed(() => {
      const group = {};

      // 필터링된 데이터에서 병원별로 병상 합산
      filteredData.value.forEach(item => {
        const hospital = item.dutyName; // 병원 이름을 기준으로 그룹화
        if (!group[hospital]) {
          group[hospital] = { usedBeds: 0, totalBeds: 0 };
        }

        // 사용 중인 병상과 총 병상 합산
        group[hospital].usedBeds += item.usedBeds || 0;
        group[hospital].totalBeds += item.totalBeds || 0; // totalBeds 합산
      });

      // 병원별로 합산된 데이터 반환
      return Object.entries(group).map(([hospital, { usedBeds, totalBeds }]) => ({
        hospital,
        usedBeds,
        totalBeds,
        usageRatio: totalBeds ? (usedBeds / totalBeds) * 100 : 0
      }));
    });

    // 색상을 조건에 맞게 설정하는 함수
    const getColorForBedUsage = (usageRatio) => {
      if (usageRatio >= 80) return 'rgba(75, 192, 192, 0.2)';  // Green
      if (usageRatio >= 50) return 'rgba(255, 159, 64, 0.2)';  // Yellow
      return 'rgba(255, 99, 132, 0.2)';  // Red
    };

    // 차트 생성 함수 (파이차트로 변경)
    const createChart = () => {
      const ctx = document.getElementById('IcuInfoPie').getContext('2d'); // canvas context 가져오기

      // 기존 차트가 있으면 파괴
      if (chartInstance) {
        chartInstance.destroy();
      }

      // 병원별로 그룹화된 데이터 준비
      const labels = groupedData.value.map(item => item.hospital);
      const usageRatios = groupedData.value.map(item => item.usageRatio);

      // 차트가 빈 데이터일 때 오류 처리
      if (labels.length === 0) {
        console.error('No data available for the chart');
        return;
      }


      // 사용 중 병상의 비율을 하나의 데이터셋에 합치기
      const chartColors = usageRatios.map(ratio => getColorForBedUsage(ratio));

      // 새로운 차트 생성 (파이차트)
      chartInstance = new Chart(ctx, {
        type: 'pie',  // 'pie'로 변경
        data: {
          labels: labels,  // 병원 이름
          datasets: [
            {
              data: usageRatios,  // 병상 사용 비율
              backgroundColor: chartColors,
              borderColor: chartColors.map(color => color.replace('0.2', '1')),  // 색상 불투명도 조정
              borderWidth: 1
            }
          ]
        },
        options: {
          responsive: true,
          plugins: {
            tooltip: {
              callbacks: {
                label: function (context) {
                  const hospital = context.label;  // 병원 이름
                  // 해당 병원의 usedBeds와 totalBeds 값을 찾기
                  const hospitalData = groupedData.value.find(item => item.hospital === hospital);
                  const usedBeds = hospitalData ? hospitalData.usedBeds : 0;  // 해당 병원의 usedBeds
                  const totalBeds = hospitalData ? hospitalData.totalBeds : 0;  // 해당 병원의 totalBeds

                  return `가용 병상 ${usedBeds} / 기준 병상 ${totalBeds}`;  // "사용중/기준 데이터" 출력
                }
              }
            },
            legend: {
              display: false // 범례를 표시하지 않음
            }
          }
        }
      });
    };

    // 필터 변경 시 차트를 갱신하는 함수
    const updateChart = () => {
      if (chartInstance) {
        createChart(); // 필터링된 데이터로 차트 갱신
      } else {
        createChart(); // 차트가 아직 생성되지 않았다면 차트 생성
      }
    };

    // 유니크한 월 목록 계산
    const uniqueMonths = computed(() => {
      return [...new Set(icuInfoList.value.map(item => {
        const dateStr = item.hvidate;  // 예: "20241223141803"
        const year = dateStr.slice(0, 4);  // 예: "2024"
        const month = dateStr.slice(4, 6); // 예: "12"
        return parseInt(month);
      }))].sort((a, b) => a - b); // 유니크한 월 정렬
    });

    // 컴포넌트 마운트 시 데이터 로드
    onMounted(() => {
      fetchIcuInfo(); // 병상 데이터 로드
    });

    // 필터 변경 시 차트 업데이트
    watch(filteredData, () => {
      updateChart(); // 필터링된 데이터로 차트 갱신
    });

    return {
      selectedDepartment, // 부서 필터 값을 반환
      selectedMonth,
      uniqueMonths,
      loading,
      filterData: updateChart, // 필터링 후 차트 갱신 함수
    };
  }
};
</script>

<style scoped>
/* 스타일 추가 가능 */
</style>
