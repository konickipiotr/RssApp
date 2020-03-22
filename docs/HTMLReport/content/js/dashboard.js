/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 6;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 84.33675757640476, "KoPercent": 15.663242423595241};
    var dataset = [
        {
            "label" : "KO",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "OK",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.750795549195908, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.9356833333333333, 500, 1500, "add to db-0"], "isController": false}, {"data": [0.6238666666666667, 500, 1500, "add to db"], "isController": false}, {"data": [0.915, 500, 1500, "clear"], "isController": false}, {"data": [1.0, 500, 1500, "send-0"], "isController": false}, {"data": [0.8913043478260869, 500, 1500, "send-1"], "isController": false}, {"data": [0.7076666666666667, 500, 1500, "HTTP Request"], "isController": false}, {"data": [0.125, 500, 1500, "send"], "isController": false}, {"data": [0.7014666666666667, 500, 1500, "add to db-1"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 93646, 14668, 15.663242423595241, 329.7199987185767, 5, 58184, 649.0, 1189.9500000000007, 3094.850000000024, 287.88819684831196, 4202.647592594479, 78.39274815566148], "isController": false}, "titles": ["Label", "#Samples", "KO", "Error %", "Average", "Min", "Max", "90th pct", "95th pct", "99th pct", "Transactions\/s", "Received", "Sent"], "items": [{"data": ["add to db-0", 30000, 0, 0.0, 212.13593333333316, 5, 7834, 713.9000000000015, 1165.9500000000007, 3486.0, 93.1118925612909, 18.27517602512314, 26.824226860918767], "isController": false}, {"data": ["add to db", 30000, 6972, 23.24, 461.6843666666652, 12, 11356, 1587.0, 2392.850000000002, 4719.960000000006, 93.10958066548521, 1866.3338166868273, 38.91689504377702], "isController": false}, {"data": ["clear", 300, 3, 1.0, 276.3999999999999, 7, 7660, 529.5000000000005, 1148.7999999999997, 4218.4200000000055, 0.9373330375526859, 1.5999286357586304, 0.11167444392717546], "isController": false}, {"data": ["send-0", 23, 0, 0.0, 13.130434782608694, 6, 71, 27.6, 62.39999999999988, 71.0, 0.07186309767444767, 0.012983079169700018, 0.00849163556504704], "isController": false}, {"data": ["send-1", 23, 0, 0.0, 233.2608695652174, 6, 2273, 990.4000000000003, 2038.3999999999967, 2273.0, 0.0718628731401576, 0.21538113490223526, 0.008210894684959414], "isController": false}, {"data": ["HTTP Request", 3000, 662, 22.066666666666666, 253.1503333333334, 6, 7651, 616.9000000000001, 930.0, 2541.869999999997, 9.322850306100252, 182.47986361446905, 1.0652084822399701], "isController": false}, {"data": ["send", 300, 59, 19.666666666666668, 7762.650000000002, 10, 58184, 22626.90000000002, 27893.649999999998, 43430.17000000003, 0.9285137280754695, 325.2516368923265, 0.1178505168339539], "isController": false}, {"data": ["add to db-1", 30000, 6972, 23.24, 249.51723333333123, 6, 7834, 757.0, 1142.0, 3401.980000000003, 93.1118925612909, 1848.1049814280573, 12.093634483058292], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Percentile 1
            case 8:
            // Percentile 2
            case 9:
            // Percentile 3
            case 10:
            // Throughput
            case 11:
            // Kbytes/s
            case 12:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 14668, 100.0, 15.663242423595241], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 93646, 14668, "500", 14668, null, null, null, null, null, null, null, null], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["add to db", 30000, 6972, "500", 6972, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["clear", 300, 3, "500", 3, null, null, null, null, null, null, null, null], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["HTTP Request", 3000, 662, "500", 662, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["send", 300, 59, "500", 59, null, null, null, null, null, null, null, null], "isController": false}, {"data": ["add to db-1", 30000, 6972, "500", 6972, null, null, null, null, null, null, null, null], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
