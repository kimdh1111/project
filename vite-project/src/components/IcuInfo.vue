<template>
  <div>
    <h1>병상 통계 차트</h1>
    <canvas id="IcuInfo" width="600" height="300"></canvas>

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
      <label for="departmentFilter">부서 필터:</label>
      <select id="departmentFilter" v-model="selectedDepartment" @change="filterData">
        <option value="">전체</option>
        <option value="일반">일반</option>
        <option value="소아">소아</option>
        <option value="응급실 음압 격리 병상">응급실 음압 격리 병상</option>
        <option value="응급실 일반 격리 병상">응급실 일반 격리 병상</option>
        <option value="분만실">분만실</option>
      </select>
    </div>

    <!-- 지역 필터 (시도 선택) -->
    <div>
      <label for="provinceFilter">시도:</label>
      <select id="provinceFilter" v-model="selectedProvince" @change="updateCities">
        <option value="">전체</option>
        <option v-for="province in uniqueProvinces" :key="province" :value="province">{{ province }}</option>
      </select>
    </div>

    <!-- 지역 필터 (시군구 선택) -->
    <div>
      <label for="cityFilter">시군구:</label>
      <select id="cityFilter" v-model="selectedCity" @change="filterData">
        <option value="">전체</option>
        <option v-for="city in filteredCities" :key="city" :value="city">{{ city }}</option>
      </select>
    </div>

    <div v-if="loading">데이터 로딩 중...</div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue';
import Chart from 'chart.js/auto';

export default {
  name: 'IcuInfo',
  setup() {
    // 데이터 관련 변수
    const icuInfoList = ref([]); // 병상 데이터 (IcuInfo)
    const selectedMonth = ref('');
    const selectedDepartment = ref('');
    const selectedProvince = ref('');
    const selectedCity = ref('');
    const loading = ref(true);
    let chartInstance = null; // 차트 인스턴스를 저장

    // API에서 병상 데이터 가져오기
    const fetchIcuInfo = async () => {
      try {
        const response = await fetch('http://localhost:8695/api/icuinfo'); // API URL 수정
        const data = await response.json();
        icuInfoList.value = data;
      } catch (error) {
        console.error('Error fetching ICU info:', error);
      } finally {
        loading.value = false;
      }
    };

    // 필터링된 데이터에 맞춰서 시군구 목록 업데이트
    const updateCities = () => {
      if (!selectedProvince.value) {
        selectedCity.value = ''; // 시도가 선택되지 않으면 시군구 초기화
      }
    };

    // 필터링된 데이터에서 고유한 시도 목록
    const uniqueProvinces = computed(() => {
      return [...new Set(icuInfoList.value
          .map(item => {
            const addr = item.dutyAddr ? item.dutyAddr.split(" ")[0] : ''; // dutyAddr에서 시도 추출
            return addr;
          })
          .filter(addr => addr.trim() !== '') // 빈 문자열이나 공백만 있는 값은 제외
      )].sort();
    });

    // 시도에 맞춰서 동적으로 시군구 목록 필터링
    const filteredCities = computed(() => {
      const cities = icuInfoList.value
          .filter(item => {
            const addr = item.dutyAddr ? item.dutyAddr.split(" ")[0] : ''; // dutyAddr에서 시도 추출
            return addr === selectedProvince.value;
          })
          .map(item => {
            const city = item.dutyAddr ? item.dutyAddr.split(" ")[1] : ''; // 시군구 추출
            return city;
          })
          .filter(addr => addr.trim() !== '') // 빈 문자열이나 공백만 있는 값은 제외
      return [...new Set(cities)].sort();
    });

    // 필터링된 데이터
    const filteredData = computed(() => {
      let filtered = icuInfoList.value;

      // 부서 필터
      if (selectedDepartment.value) {
        filtered = filtered.filter(item =>
            item.department.toLowerCase() === selectedDepartment.value.toLowerCase()
        );
      }

      // 시도 필터
      if (selectedProvince.value) {
        filtered = filtered.filter(item => {
          const addr = item.dutyAddr ? item.dutyAddr.split(" ")[0] : ''; // dutyAddr에서 시도 추출
          return addr === selectedProvince.value;
        });
      }

      // 시군구 필터
      if (selectedCity.value) {
        filtered = filtered.filter(item => {
          const addr = item.dutyAddr ? item.dutyAddr.split(" ")[1] : ''; // dutyAddr에서 시군구 추출
          return addr === selectedCity.value;
        });
      }

      // 월 필터
      if (selectedMonth.value) {
        filtered = filtered.filter(item => {
          const dateStr = item.hvidate; // 예: "20241223141803"

          // hvidate에서 연, 월, 일을 추출하여 Date 객체로 변환
          const year = dateStr.slice(0, 4);  // 예: "2024"
          const month = dateStr.slice(4, 6); // 예: "12"

          // 월을 추출하고 필터링
          return parseInt(month) === parseInt(selectedMonth.value);
        });
      }

      return filtered;
    });

    // 병원별로 병상 데이터 합산
    const groupedData = computed(() => {
      const group = {};

      filteredData.value.forEach(item => {
        const hospital = item.dutyName; // 병원 이름을 기준으로 그룹화
        if (!group[hospital]) {
          group[hospital] = {availableBeds: 0, usedBeds: 0};
        }

        group[hospital].availableBeds += item.availableBeds || 0;
        group[hospital].usedBeds += item.usedBeds || 0;
      });

      return Object.entries(group).map(([hospital, {availableBeds, usedBeds}]) => ({
        hospital,
        availableBeds,
        usedBeds
      }));
    });

    // 차트 생성 함수
    const createChart = () => {
      const ctx = document.getElementById('IcuInfo').getContext('2d'); // canvas context 가져오기

      if (chartInstance) {
        chartInstance.destroy();
      }

      const labels = groupedData.value.map(item => item.hospital);
      const availableBeds = groupedData.value.map(item => item.availableBeds);
      const usedBeds = groupedData.value.map(item => item.usedBeds);

      if (labels.length === 0) {
        console.error('No data available for the chart');
        return;
      }

      chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: '사용 가능 병상',
              data: availableBeds,
              backgroundColor: 'rgba(75, 192, 192, 0.2)',
              borderColor: 'rgba(75, 192, 192, 1)',
              borderWidth: 1,
              barThickness: 10,
            },
            {
              label: '사용 중 병상',
              data: usedBeds,
              backgroundColor: 'rgba(255, 99, 132, 0.2)',
              borderColor: 'rgba(255, 99, 132, 1)',
              borderWidth: 1,
              barThickness: 10,
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            x: {
              display: false,
            },
            y: {
              beginAtZero: true
            }
          }
        }
      });
    };

    // 차트 업데이트 함수
    const updateChart = () => {
      if (chartInstance) {
        const updatedData = groupedData.value;
        chartInstance.data.labels = updatedData.map(item => item.hospital);
        chartInstance.data.datasets[0].data = updatedData.map(item => item.availableBeds);
        chartInstance.data.datasets[1].data = updatedData.map(item => item.usedBeds);
        chartInstance.update();
      }
    };

    // 유니크한 월 목록 계산
    const uniqueMonths = computed(() => {
      return [...new Set(icuInfoList.value.map(item => {
        const dateStr = item.hvidate;
        const month = dateStr.slice(4, 6); // 예: "12"
        return parseInt(month);
      }))].sort((a, b) => a - b);
    });

    // 컴포넌트 마운트 시 데이터 로드
    onMounted(() => {
      fetchIcuInfo();
    });

    // 필터링된 데이터 변화 시 차트 업데이트
    watch(filteredData, () => {
      if (chartInstance) {
        updateChart();
      } else {
        createChart();
      }
    });

    return {
      selectedDepartment,
      selectedMonth,
      selectedProvince,
      selectedCity,
      uniqueMonths,
      uniqueProvinces,
      filteredCities,
      loading,
      filterData: updateChart,
      updateCities,
    };
  }
};
</script>

<style scoped>
/* 스타일 추가 가능 */
</style>
