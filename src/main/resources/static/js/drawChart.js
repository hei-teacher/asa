$(document).ready(function () {
  google.charts.load("current", { packages: ["corechart", "bar"] });
  google.charts.setOnLoadCallback(drawChart);
});
function drawChart() {
  const productTitle = `Executed Days for each Product - ${workerName}`;
  const missionTitle = `Executed Days for each Mission - ${workerName}`;
  const missionProductTitle = `Executed Days for each Mission of each Product - ${workerName}`;

  const data1 = new google.visualization.DataTable();
  data1.addColumn("string", "Product");
  data1.addColumn("number", "Executed days");
  productChartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data1.addRow([label, item.executedDays]);
  });
  const options1 = getPieChartOptions(productTitle);
  const container1 = document.getElementById("chart-1");
  chartInstance1 = new google.visualization.PieChart(container1);
  chartInstance1.draw(data1, options1);

  const data2 = new google.visualization.DataTable();
  data2.addColumn("string", "Product");
  data2.addColumn("number", "Executed days");
  data2.addColumn("number", "Student executed days");
  productChartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data2.addRow([label, item.executedDays, item.studentExecutedDays]);
  });
  const options2 = getBarChartOptions(productTitle);
  const container2 = document.getElementById("chart-2");
  chartInstance2 = new google.visualization.ColumnChart(container2);
  chartInstance2.draw(data2, options2);

  const data3 = new google.visualization.DataTable();
  data3.addColumn("string", "Product");
  data3.addColumn("number", "Executed days");
  missionChartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data3.addRow([label, item.executedDays]);
  });
  const options3 = getPieChartOptions(missionTitle);
  const container3 = document.getElementById("chart-3");
  chartInstance3 = new google.visualization.PieChart(container3);
  chartInstance3.draw(data3, options3);

  const data4 = new google.visualization.DataTable();
  data4.addColumn("string", "Product");
  data4.addColumn("number", "Executed days");
  data4.addColumn("number", "Student executed days");
  missionChartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data4.addRow([label, item.executedDays, item.studentExecutedDays]);
  });
  const options4 = getBarChartOptions(missionTitle);
  const container4 = document.getElementById("chart-4");
  chartInstance4 = new google.visualization.ColumnChart(container4);
  chartInstance4.draw(data4, options4);

  const data5 = new google.visualization.DataTable();
  data5.addColumn("string", "Product");
  data5.addColumn("number", "Executed days");
  missionPerProductChartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data5.addRow([label, item.executedDays]);
  });
  const options5 = getPieChartOptions(missionProductTitle);
  const container5 = document.getElementById("chart-5");
  chartInstance5 = new google.visualization.PieChart(container5);
  chartInstance5.draw(data5, options5);

  const data6 = new google.visualization.DataTable();
  data6.addColumn("string", "Product");
  data6.addColumn("number", "Executed days");
  data6.addColumn("number", "Student executed days");
  missionPerProductChartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data6.addRow([label, item.executedDays, item.studentExecutedDays]);
  });
  const options6 = getBarChartOptions(missionProductTitle);
  const container6 = document.getElementById("chart-6");
  chartInstance6 = new google.visualization.ColumnChart(container6);
  chartInstance6.draw(data6, options6);

  return [
    chartInstance1,
    chartInstance2,
    chartInstance3,
    chartInstance4,
    chartInstance5,
    chartInstance6,
  ];
}

function getBarChartOptions(title) {
  return {
    backgroundColor: "#f9f9f9",
    title: title,
    colors: ["#d52257", "#138cf1"],
  };
}

function getPieChartOptions(title) {
  return {
    backgroundColor: "#f9f9f9",
    title: title,
    legend: {
      position: "right",
      alignment: "center",
      textStyle: {
        fontSize: 11,
        bold: true,
        color: "#4a5568",
        fontName: "Arial",
      },
    },
  };
}
