<template>
  <div>
    <h1>월별 병상 통계 차트</h1>
    <canvas id="bedChart" width="400" height="200"></canvas>

    <!-- 중증도 필터 -->
    <div>
      <label for="severityFilter">중증도 필터:</label>
      <select id="severityFilter" v-model="selectedSeverity" @change="filterData">
        <option value="">전체</option>
        <option value="mild">내과</option>
        <option value="moderate">소아과</option>
        <option value="critical">응급실</option>
      </select>
    </div>

    <!-- 월별 필터 -->
    <div>
      <label for="monthFilter">월 필터:</label>
      <select id="monthFilter" v-model="selectedMonth" @change="filterData">
        <option value="">전체</option>
        <!-- 동적으로 생성된 월 옵션을 사용 -->
        <option v-for="month in uniqueMonths" :key="month" :value="month">
          {{ month }}월
        </option>
      </select>
    </div>
    <!-- 로딩 상태 -->
    <div v-if="loading">데이터 로딩 중...</div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import Chart from 'chart.js/auto';

export default {
  name: 'BedChart',
  setup() {
    const bedInfoList = ref([]);  // 병상 데이터
    const selectedSeverity = ref('');  // 중증도 필터
    const selectedMonth = ref('');  // 월 필터
    const loading = ref(true); // 로딩 상태
    let chartInstance = null;  // 차트 인스턴스를 저장

    // 병상 데이터를 API에서 가져오는 함수
    const fetchBedInfo = async () => {
      if (bedInfoList.value.length > 0) return;  // 이미 데이터가 있으면 호출하지 않음
      try {
        const response = await fetch('http://localhost:8695/api/bedinfo');
        const data = await response.json();
        bedInfoList.value = data;  // 병상 데이터 저장
        console.log('Fetched bed info:', bedInfoList.value);  // 디버깅용 로그
        processMonthlyData();  // 초기 데이터로 차트 처리
      } catch (error) {
        console.error('Error fetching bed info:', error);
      } finally {
      loading.value = false;
    }
    };

    // 데이터 필터링 및 차트 업데이트
    const filterData = () => {
      let filteredData = bedInfoList.value;

      // 중증도 필터링
      if (selectedSeverity.value) {
        filteredData = filteredData.filter(item =>
            item.severity && item.severity.toLowerCase() === selectedSeverity.value.toLowerCase()
        );
      }

      // 월 필터링
      if (selectedMonth.value) {
        filteredData = filteredData.filter(item => {
          const callDate = new Date(item.calltime);  // calltime을 Date 객체로 변환
          const month = callDate.getMonth() + 1;  // getMonth()는 0부터 시작하므로 +1을 해줍니다.
          return month === parseInt(selectedMonth.value);  // 선택된 월과 비교
        });
      }

      // 필터링된 데이터로 월별 데이터 처리
      processMonthlyData(filteredData);
    };

    // 월별로 병상 데이터를 처리하는 함수
    const processMonthlyData = (filteredData = bedInfoList.value) => {
      const monthlyData = {};

      // 데이터를 월별로 그룹화
      filteredData.forEach(item => {
        const callDate = new Date(item.calltime);  // calltime을 Date 객체로 변환
        const month = callDate.getMonth() + 1;  // 월을 추출 (1월=1, 2월=2, ...)

        // 기본값 설정: `availableBeds`와 `usedBeds`가 숫자가 아닐 경우 0으로 처리
        const availableBeds = isNaN(item.availableBeds) ? 0 : item.availableBeds;
        const usedBeds = isNaN(item.usedBeds) ? 0 : item.usedBeds;

        if (!monthlyData[month]) {
          monthlyData[month] = {availableBeds: 0, usedBeds: 0};
        }

        monthlyData[month].availableBeds += availableBeds;
        monthlyData[month].usedBeds += usedBeds;
      });

      const labels = Object.keys(monthlyData);
      const availableBeds = labels.map(month => monthlyData[month].availableBeds);
      const usedBeds = labels.map(month => monthlyData[month].usedBeds);

      // 차트 생성
      createOrUpdateChart(labels, availableBeds, usedBeds);
    };

    // 차트 생성 및 업데이트 함수
    const createOrUpdateChart = (labels, availableBeds, usedBeds) => {
      const ctx = document.getElementById('bedChart').getContext('2d');

      // 기존 차트가 있으면 데이터를 업데이트하고, 그렇지 않으면 새로 생성
      if (chartInstance) {
        chartInstance.data.labels = labels;
        chartInstance.data.datasets[0].data = availableBeds;
        chartInstance.data.datasets[1].data = usedBeds;
        chartInstance.update();  // 데이터만 갱신
      } else {
        // 차트가 없으면 새로 생성
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
              },
              {
                label: '사용 중 병상',
                data: usedBeds,
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 1,
              }
            ]
          },
          options: {
            responsive: true,
            scales: {
              y: {
                beginAtZero: true,
              }
            }
          }
        });
      }
    };

    // 고유한 월 값을 추출하여 필터에 사용할 수 있도록 하는 계산된 속성
    const uniqueMonths = computed(() => {
      const months = bedInfoList.value.map(item => {
        const callDate = new Date(item.calltime);  // calltime을 Date 객체로 변환
        return callDate.getMonth() + 1;  // 0부터 시작하는 월을 +1로 변환
      });

      // 중복된 월을 제거하고 오름차순으로 정렬
      return [...new Set(months)].sort((a, b) => a - b);
    });

    // 컴포넌트 마운트 시 데이터 로드
    onMounted(() => {
      fetchBedInfo();
    });

    return {
      selectedSeverity,
      selectedMonth,
      uniqueMonths,
      loading,
      filterData
    };
  }
};
</script>

<style scoped>
/* 스타일 추가 가능 */
</style>
