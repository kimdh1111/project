import { createRouter, createWebHistory } from 'vue-router';


const BedChart = () => import('../components/BedChart.vue');
const BedStatus = () => import('../components/BedStatus.vue');
const IcuInfo = () => import('../components/IcuInfo.vue')
const IcuInfoPie = () => import('../components/IcuInfoPie.vue')
const routes = [

    { path: '/chart', component: BedChart },
    { path: '/status', component: BedStatus },
    { path: '/Icu', component: IcuInfo },
    { path: '/IcuPie', component: IcuInfoPie },
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),  // history 모드
    routes,  // 라우트 설정
});

export default router;