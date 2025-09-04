function downloadPieChart() {
  console.log(pieChartProductInstance);
  if (!pieChartProductInstance) return;
  const imageURI = pieChartProductInstance.getImageURI();
  const link = document.createElement("a");
  link.href = imageURI;

  const now = new Date();
  const timestamp = now.toISOString().slice(0, 10);
  const code =
    workerCode && workerCode.trim() ? workerCode.trim() : "all_workers";

  link.download = `pie_chart_${code}_${timestamp}.png`;
  link.click();
}

function downloadBarChart(barChartInstance) {
  if (!barChartInstance) return;
  const imageURI = barChartInstance.getImageURI();
  const link = document.createElement("a");
  link.href = imageURI;

  const now = new Date();
  const timestamp = now.toISOString().slice(0, 10);
  const code =
    workerCode && workerCode.trim() ? workerCode.trim() : "all_workers";

  link.download = `bar_chart_${code}_${timestamp}.png`;
  link.click();
}
