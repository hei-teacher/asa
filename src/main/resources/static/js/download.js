function downloadPieChart() {
  if (!pieChartInstance) return;
  const imageURI = pieChartInstance.getImageURI();
  const link = document.createElement("a");
  link.href = imageURI;

  const now = new Date();
  const timestamp = now.toISOString().slice(0, 10);
  const code =
    workerCode && workerCode.trim() ? workerCode.trim() : "all_workers";

  link.download = `products_pie_chart_${code}_${timestamp}.png`;
  link.click();
}

function downloadColumnChart() {
  if (!barChartInstance) return;
  const imageURI = barChartInstance.getImageURI();
  const link = document.createElement("a");
  link.href = imageURI;

  const now = new Date();
  const timestamp = now.toISOString().slice(0, 10);
  const code =
    workerCode && workerCode.trim() ? workerCode.trim() : "all_workers";

  link.download = `products_bar_chart_${code}_${timestamp}.png`;
  link.click();
}
