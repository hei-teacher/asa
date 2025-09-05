function downloadChart(instance) {
  if (!instance) return;
  const imageURI = instance.getImageURI();
  const link = document.createElement("a");
  link.href = imageURI;

  const now = new Date();
  const timestamp = now.toISOString().slice(0, 10);
  const code =
    workerCode && workerCode.trim() ? workerCode.trim() : "all_workers";

  link.download = `chart_${code}_${timestamp}.png`;
  link.click();
}
