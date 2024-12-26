<template>
  <div>
    <h1>병상 상태 통계 차트</h1>
    <canvas id="bedStatusChart" width="800" height="400"></canvas>

    <!-- 필터 UI -->
    <div>
      <label for="monthFilter">월:</label>
      <select id="monthFilter" v-model="selectedMonth" @change="filterData">
        <option value="">전체</option>
        <option v-for="month in uniqueMonths" :key="month" :value="month">{{ month }}월</option>
      </select>
    </div>

    <div>
      <label for="statusFilter">상태:</label>
      <select id="statusFilter" v-model="selectedStatus" @change="filterData">
        <option value="">전체</option>
        <option value="mild">입원실 음압격리</option>
        <option value="moderate">중환자실 일반격리</option>
        <option value="critical">중환자실 음압격리</option>
      </select>
    </div>

    <div>
      <label for="regionFilter">지역:</label>
      <select id="regionFilter" v-model="selectedRegion" @change="filterData">
        <option value="">전체</option>
        <option v-for="region in uniqueDutyAddrs" :key="region" :value="region">{{ region }}</option>
      </select>
    </div>

    <div v-if="loading">데이터 로딩 중...</div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import Chart from 'chart.js/auto';

export default {
  name: 'BedStatus',
  setup() {
    const bedStatusList = ref([]);
    const selectedMonth = ref('');
    const selectedStatus = ref('');
    const selectedRegion = ref('');
    const loading = ref(true);
    let chartInstance = null;

    const fetchBedStatus = async () => {
      try {
        const response = await fetch('http://localhost:8695/api/bedstatus');
        const data = await response.json();
        bedStatusList.value = data;
        filterData();
      } catch (error) {
        console.error('Error fetching bed status:', error);
      } finally {
        loading.value = false;
      }
    };

    const filterData = () => {
      let filteredData = bedStatusList.value;

      // 상태 필터
      if (selectedStatus.value) {
        filteredData = filteredData.filter(item =>
            item.status.toLowerCase().includes(selectedStatus.value.toLowerCase())
        );
      }

      // 월 필터
      if (selectedMonth.value) {
        filteredData = filteredData.filter(item => {
          const callDate = new Date(item.calltime);
          return callDate.getMonth() + 1 === parseInt(selectedMonth.value);
        });
      }

      // 지역 필터 (dutyAddr의 앞 두 단어만 추출하여 selectedRegion과 비교)
      if (selectedRegion.value) {
        filteredData = filteredData.filter(item => {
          const addr = item.dutyAddr ? item.dutyAddr.split(" ").slice(0, 2).join(" ") : '';
          return addr.includes(selectedRegion.value);  // selectedRegion 값과 비교
        });
      }


      // 데이터가 존재하면 차트 그리기, 없으면 차트를 초기화
      if (filteredData.length > 0) {
        processBedData(filteredData);
      } else {
        clearChart();
      }
    };

    const processBedData = (filteredData) => {
      const labels = filteredData.map(item => item.dutyName);
      const availableBeds = filteredData.map(item => item.availableBeds || 0);  // 0을 기본값으로 처리
      const usedBeds = filteredData.map(item => item.usedBeds || 0);  // 0을 기본값으로 처리

      createChart(labels, availableBeds, usedBeds);
    };

    const createChart = (labels, availableBeds, usedBeds) => {
      const ctx = document.getElementById('bedStatusChart').getContext('2d');
      if (chartInstance) chartInstance.destroy();  // 기존 차트가 있으면 파괴

      chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: labels,  // x축 (병원 이름)
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
            y: {
              beginAtZero: true,  // Y축은 0에서 시작
            }
          }
        }
      });
    };

    const clearChart = () => {
      const ctx = document.getElementById('bedStatusChart').getContext('2d');
      if (chartInstance) chartInstance.destroy();  // 차트를 파괴하여 초기화
      chartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['No data'],  // 데이터 없음 표시
          datasets: [
            {
              label: '사용 가능 병상',
              data: [0],
              backgroundColor: 'rgba(75, 192, 192, 0.2)',
              borderColor: 'rgba(75, 192, 192, 1)',
              borderWidth: 1,
            },
            {
              label: '사용 중 병상',
              data: [0],
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
    };

    const uniqueMonths = computed(() => {
      return [...new Set(bedStatusList.value.map(item => new Date(item.calltime).getMonth() + 1))].sort((a, b) => a - b);
    });



    const uniqueDutyAddrs = computed(() => {
      return [...new Set(bedStatusList.value
          .map(item => item.dutyAddr ? item.dutyAddr.split(" ").slice(0, 2).join(" ") : '') // dutyAddr에서 시도와 시군구 추출
          .filter(addr => addr.trim() !== '') // 빈 문자열이나 공백만 있는 값은 제외
      )].sort();
    });

    onMounted(() => {
      fetchBedStatus();
    });

    return {
      selectedStatus,
      selectedMonth,
      selectedRegion,
      uniqueMonths,
      uniqueDutyAddrs,
      loading,
      filterData,
    };
  }
};
</script>

<style scoped>
/* 필요시 스타일 추가 */
</style>
