function updateFilters() {
  const workerCode = document.getElementById("worker").value;
  window.location.href = `/missions?workerCode=${workerCode}`;
}

document.addEventListener("DOMContentLoaded", function () {
  const workerSelect = document.getElementById("worker");
  if (workerSelect) {
    workerSelect.addEventListener("change", updateFilters);
  }
  const showGraphBtn = document.getElementById("showGraphBtn");
  const graphModal = document.getElementById("graphModal");
  const closeModal = document.getElementById("closeModal");

  showGraphBtn.addEventListener("click", function () {
    graphModal.classList.remove("hidden");
    setTimeout(() => {
      drawPieChart(
        productChartData,
        pieChartProductContainer,
        productTitle,
        pieChartProductInstance,
      );
      drawPieChart(
        missionChartData,
        pieChartMissionContainer,
        missionTitle,
        pieChartMissionInstance,
      );
      drawBarChart(
        productChartData,
        barChartProductContainer,
        productTitle,
        barChartProductInstance,
      );
      drawBarChart(
        missionChartData,
        barChartMissionContainer,
        missionTitle,
        barChartMissionInstance,
      );
    }, 200);
  });

  closeModal.addEventListener("click", function () {
    graphModal.classList.add("hidden");
  });
  graphModal.addEventListener("click", function (e) {
    if (e.target === graphModal) {
      graphModal.classList.add("hidden");
    }
  });

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && !graphModal.classList.contains("hidden")) {
      graphModal.classList.add("hidden");
    }
  });
  let resizeTimeout;
  window.addEventListener("resize", function () {
    if (!graphModal.classList.contains("hidden")) {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(function () {
        drawPieChart(
          productChartData,
          pieChartProductContainer,
          productTitle,
          pieChartProductInstance,
        );
        drawPieChart(
          missionChartData,
          pieChartMissionContainer,
          missionTitle,
          pieChartMissionInstance,
        );
        drawBarChart(
          productChartData,
          barChartProductContainer,
          productTitle,
          barChartProductInstance,
        );
        drawBarChart(
          missionChartData,
          barChartMissionContainer,
          missionTitle,
          barChartMissionInstance,
        );
      }, 250);
    }
  });
});
