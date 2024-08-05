import React, {useEffect, useState} from "react";
import {HOST_NAME, STORE_ID, UPC_NBR, SERVICE_PROTOCOL} from "../constants";
import {
    Chart as ChartJS,
    LineElement,
    TimeScale, // x axis
    LinearScale,   // y axis
    PointElement,
    Tooltip,
    Legend, ChartData
} from 'chart.js/auto';
import 'chartjs-adapter-luxon';
// import { StreamingPlugin } from 'chartjs-plugin-streaming';
// import ChartStreaming from 'chartjs-plugin-streaming';

import { Line } from "react-chartjs-2";
import {ItemInventorySnapshot} from "../model";

import {DateTime, DateTimeFormatOptions} from "luxon";

ChartJS.register(
        // StreamingPlugin,
        // ChartStreaming,
        LineElement,
        TimeScale,
        LinearScale,
        PointElement,
        Tooltip,
        Legend
);

type SnapshotDataPoint = {
    x: string | "Invalid DateTime";
    y: number
}

const dtFormatOpts: DateTimeFormatOptions = { hour: '2-digit', minute: '2-digit', hourCycle: 'h24' };
const toDataPoint = (snapshot: ItemInventorySnapshot): SnapshotDataPoint => ({
    x: snapshot.timestamp === undefined ? "" : snapshot.timestamp.toLocaleString(dtFormatOpts),
    y: snapshot.count
});

const fetchSnapshotCounts = async () => {

    const url = `${SERVICE_PROTOCOL}://${HOST_NAME}/inventory/stores/${STORE_ID}/upc/${UPC_NBR}/snapshots`;

    console.info(`Calling item inventory service: ${url}`);
    const response = await fetch(url);
    const payload = await response.json();
    let data: ItemInventorySnapshot[] = [];
    if (payload !== undefined) {
        console.log(JSON.stringify(payload));
        data = payload.map((i: { upc: string; timestamp: string; count: number; }) => {
            const snap: ItemInventorySnapshot = {
                upc: i.upc,
                timestamp: DateTime.fromISO(i.timestamp),
                count: i.count
            };
            return snap;
        });
    }

    return data.map(toDataPoint);

        //labels: data.map((snap) => snap.timestamp.toLocaleString(dtFormatOpts)),
        // datasets: [
        //     {
        //         label: "Inventory Count",
        //         data: data.map((item) => item.count),
        //         backgroundColor: [
        //             "rgba(75,192,192,1)",
        //             "#ecf0f1",
        //             "#50AF95",
        //             "#f3ba2f",
        //             "#2a71d0"
        //         ],
        //         borderColor: "black",
        //         borderWidth: 2
        //     }
        // ]
    // };
};

const fetchChartData = async () => {
    const data = await fetchSnapshotCounts();
    return {
        labels: data.map((dp) => dp.x),
        datasets: [
            {
                label: "Item Inventory",
                data: data.map((dp) => dp.y),
                backgroundColor: [
                    "rgba(75,192,192,1)",
                    // "#ecf0f1",
                    // "#50AF95",
                    // "#f3ba2f",
                    // "#2a71d0"
                ],
                borderColor: "black",
                borderWidth: 2
            }
        ]
    };
};

const refreshChartOptions = async (minY: number, maxY: number) => {
    return {
        elements: {
            line: {
                tension: 0.4
            }
        },
        scales: {
            y: {
                min: minY === undefined ? 0 : minY,
                max: maxY === undefined ? 20 : maxY,
                beginAtZero: true,
            }
        }
    }
};



/*const onRefresh = (chart: ChartJS) => {
    const dataPoints = await fetchSnapshotCounts()
    dataPoints.forEach(pt =>    chart.config.data.datasets[0].data.push({
        x: pt.x.toFormat("h:MM"), y: pt.y
    });
            .forEach(dataset => {

        dataset.data.push({
            x: Date.now(),
            y: randomScalingFactor()
        });
    });
};*/


const LineChart = () => {
    const [setInventoryCounts, inventoryCounts] = React.useState<ItemInventorySnapshot[]>([]);
    const [loading, setLoading] = useState(false);

    const [chartData, setChartData] = useState({
        labels: [],
        datasets: [
            {
                label: "Inventory Count",
                data: [],
                backgroundColor: [],
                borderColor: "black",
                borderWidth: 2
            }
        ]
    });

    const [chartOptions, setChartOptions] = React.useState({
            elements: {
                line: {
                    tension: 0.4
                }
            },
            scales: {
                y: {
                    min: 0,
                    max: 20,
                    beginAtZero: true,
                }
            }
    });

    const refreshChartData = async () => {
        console.debug("Refreshing the chart data")
        try {
            const data = await fetchChartData();
            const maxY = Math.max(...(data.datasets[0].data)) + 2;
            const minYAxisValue = Math.min(...(data.datasets[0].data));
            const minY = minYAxisValue < 1 ? 0 : minYAxisValue - 2;
            // @ts-ignore
            setChartData(data);
            setChartOptions(await refreshChartOptions(minY, maxY));
        } catch (error) {
            console.error("Error fetching chart data:", error);
        }
    };

    useEffect(() => {
        setLoading(true);
        refreshChartData();
        setLoading(false);
    });
        //
    //     fetch(url)
    //             .then(response => response.json())
    //             .then(payload => {
    //                 let inventorySnapshots: ItemInventorySnapshot[] = [];
    //                 if (payload !== undefined) {
    //                     console.log(JSON.stringify(payload));
    //                     inventorySnapshots = payload.map((i: ItemInventorySnapshot) => {
    //                         const snap: ItemInventorySnapshot = {
    //                             upc: i.upc,
    //                             timestamp: i.timestamp,
    //                             count: i.count
    //                         };
    //                         return snap;
    //                     });
    //                 }
    //                 // @ts-ignore
    //                 setInventoryCounts(inventorySnapshots);
    //                 // @ts-ignore
    //                 setChartDataPoints(inventory.map(toDataPoint));
    //                 setLoading(false);
    //             })
    //             .catch(error => {
    //                 console.error('Error:', error);
    //             });
    // }, []);

    // if (loading) {
    //     return <p>Loading...</p>;
    // }

    // const data = {
    //     datasets: [{
    //         label: 'Inventory Changes',
    //         data: chartDataPoints,
    //         backgroundColor: 'rgba(255, 255, 255, 0.6)',
    //         borderColor: 'black',
    //         borderWidth: 1,
    //         tension: 0.4
    //     }]
    // };

    // const computeMaxCount = (points: SnapshotDataPoint[]): number => {
    //     const counts: number[] = points.map(p => p.y);
    //     return Math.max(...counts);
    // }
    //
    // const computeMinCount = (points: SnapshotDataPoint[]): number => {
    //     const counts: number[] = points.map(p => p.y);
    //     return Math.min(...counts);
    // }


    //     scales: {
    //         x: {
    //            type: "realtime",
    //            distribution: "linear",
    //             realtime: {
    //                 delay: 3000,
    //                 onRefresh: chart => {
    //                         chart.data.datasets[0].data.push({
    //                             x: moment(),
    //                             y: Math.random()
    //                         });
    //                     },
    //
    //                     time: {
    //                         displayFormat: "h:mm"
    //                     }
    //                 },
    //                 ticks: {
    //                     displayFormats: 1,
    //                     maxRotation: 0,
    //                     minRotation: 0,
    //                     stepSize: 1,
    //                     maxTicksLimit: 30,
    //                     minUnit: "second",
    //                     source: "auto",
    //                     autoSkip: true,
    //                     callback: function(value) {
    //                         return DateTime(value, "HH:mm:ss").format("mm:ss");
    //                     }
    //                 }
    //             }
    //         ],
    //
    //
    //
    //
    //     animation: {
    //         duration: 2000
    //     },
    //     responsive: true,
    //     maintainAspectRatio: true,
    //     tooltips: {
    //         mode: "x",
    //         intersect: false
    //     },
    //     legend: {
    //         display: false
    //     },
    //     scales: {
    //         x: [
    //         {
    //             display: true,
    //             type: 'time',
    //             time: {
    //                 unit: "second",
    //                 unitStepSize: 120
    //             },
    //             ticks: {
    //                 fontSize: 13
    //             }
    //         }],
    //         y: [
    //         {
    //             display: true,
    //             ticks: {
    //                 fontSize: 13,
    //                 beginAtZero: true,
    //                 // callback: function (value, index, values) {
    //                 //     return value + "%";
    //                 // }
    //             }
    //         }]
    //     }
    //     // plugins: {
    //     //     title: {
    //     //         display: true,
    //     //                 text: "Users Gained between 2016-2020"
    //     //     },
    //     //     legend: {
    //     //         display: false
    //     //     }
    //     // }
    // };

    return (
            <div className="chart-container">
                <h2 style={{textAlign: "center"}}></h2>
                <Line
                        data={chartData}
                        options={chartOptions}
                />
                <button onClick={refreshChartData}>Refresh Chart</button>
            </div>
    );
}
export default LineChart;