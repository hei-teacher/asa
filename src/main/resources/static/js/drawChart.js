$(document).ready(function () {
  google.charts.load("current", { packages: ["corechart", "bar"] });
  google.charts.setOnLoadCallback();
});
function drawBarChart(chartData, container, title, chartInstance) {
  var data = new google.visualization.DataTable();
  data.addColumn("string", "Product");
  data.addColumn("number", "Executed days");
  data.addColumn("number", "Student executed days");

  chartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    data.addRow([label, item.executedDays, item.studentExecutedDays]);
  });

  var options = {
    backgroundColor: "#f9f9f9",
    title: title,
    colors: ["#d52257", "#138cf1"],
  };

  if (!chartInstance) {
    chartInstance = new google.visualization.ColumnChart(container);
  }
  chartInstance.draw(data, options);
}
function drawPieChart(chartData, container, title, chartInstance) {
  const chartDataTable = new google.visualization.DataTable();
  chartDataTable.addColumn("string", "ThProduct");
  chartDataTable.addColumn("number", "Executed Days");

  chartData.forEach(function (item) {
    const label = item.code + " - " + item.name;
    chartDataTable.addRow([label, item.executedDays]);
  });

  const options = {
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

  if (!chartInstance) {
    chartInstance = new google.visualization.PieChart(container);
  }
  chartInstance.draw(chartDataTable, options);
}
