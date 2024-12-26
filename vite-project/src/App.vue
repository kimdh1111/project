<script setup>




</script>



<template>

  <div id="app">
    <nav>
      <ul>

        <li><router-link to="/status">status</router-link></li>
        <li><router-link to="/chart">chart</router-link></li>
        <li><router-link to="/Icu">Icu</router-link></li>
        <li><router-link to="/IcuPie">IcuPie</router-link></li>
      </ul>
    </nav>
    <!-- 페이지 콘텐츠 렌더링 -->
    <router-view />





  </div>

<!--  <div>-->
<!--    <a href="https://vite.dev" target="_blank">-->
<!--      <img src="/vite.svg" class="logo" alt="Vite logo" />-->
<!--    </a>-->
<!--    <a href="https://vuejs.org/" target="_blank">-->
<!--      <img src="./assets/vue.svg" class="logo vue" alt="Vue logo" />-->
<!--    </a>-->
<!--  </div>-->
<!--  <HelloWorld msg="Vite + Vue" />-->
</template>

<style scoped>
.logo {
  height: 6em;
  padding: 1.5em;
  will-change: filter;
  transition: filter 300ms;
}
.logo:hover {
  filter: drop-shadow(0 0 2em #646cffaa);
}
.logo.vue:hover {
  filter: drop-shadow(0 0 2em #42b883aa);
}


</style>
