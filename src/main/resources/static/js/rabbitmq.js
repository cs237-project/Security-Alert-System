$(document).ready(function () {
    $("#createMessage").click(function () {
        let emergencyType = $('#securityIssue option:selected').text();
        console.log("emergencyType:"+ emergencyType);


        if (emergencyType === "Please select") {
            $("#createRabbitMQMessage").text("You need to input a valid emergency type.");
        } else {
            let clientUrl = "/client/getClients";
            let messageUrl = "/rabbitmq/messageSender/create/"+emergencyType;
            console.log(clientUrl);
            console.log(messageUrl);
            $.ajax({
                type: "GET",
                url: messageUrl,
                dataType: "json",
                success: function (response) {
                    $("#createRabbitMQMessage").text(response.message+" The emergency happens at x: "+response.data[0]+
                    " y: "+response.data[1]);
                    let data = response.data;
                    // let data = response.data;
                    sessionStorage.setItem("latitude", data[0]);
                    sessionStorage.setItem("longitude", data[1]);
                    // console.log("succesfully added clients");
                    // console.log("latitude:" + data[0]);
                    // console.log("longitude:" + data[1]);
                },
                error: function () {
                    $("#createRabbitMQMessage").text("something went wrong when creating messages");
                    console.log("something went wrong when creating messages");
                }
            });
            $.ajax({
                type: "GET",
                url: clientUrl,
                dataType: "json",
                success: function (clientResponse) {
                    messageHandle(emergencyType,clientResponse.data);
                },
                error: function () {
                    $("#createRabbitMQMessage").text("something went wrong when creating messages");
                    console.log("something went wrong when creating messages");
                }
            });
        }

    });

    function messageHandle(emergencyType,client) {
        const ctx = document.getElementById("emergency-location").getContext('2d');
        const data = client;
        let emergencyLocation = [];
        emergencyLocation.push({id: emergencyType,
            x:sessionStorage.getItem("latitude"),y:sessionStorage.getItem("longitude")});
        let clientLocation = [];
        if(emergencyType === "Robbery" || emergencyType === "Sexual Assault" || emergencyType === "Gun Shot"){
            for (let i = 0; i < data.length; i++) {
                clientLocation.push({id: data[i]['clientId'], x:data[i]['locationx'], y: data[i]['locationy']});
            }
        }else{
            for (let i = 0; i < data.length; i++) {
                clientLocation.push({id: data[i]['clientId'], x:data[i]['addressx'], y: data[i]['addressy']});
            }
        }
        let circle1 = getLocArr(1000,15,sessionStorage.getItem("latitude"),sessionStorage.getItem("longitude"));
        let circle2 = getLocArr(1000,20,sessionStorage.getItem("latitude"),sessionStorage.getItem("longitude"));
        var myChart = new Chart(ctx, {
            type: 'scatter',
            data: {
                label: 'current location and home location Dataset',
                datasets: [{
                    data: clientLocation,
                    label: 'client location',
                    backgroundColor: 'rgba(0, 150, 150, 1)',
                },{
                    data:emergencyLocation,
                    label: 'emergency location',
                    backgroundColor: 'rgba(255, 0, 0, 1)',
                },{
                    data:circle1,
                    label: 'boundary1',
                    backgroundColor: 'rgba(255, 255, 0, 1)',
                },{
                    data:circle2,
                    label: 'boundary2',
                    backgroundColor: 'rgba(255, 255, 0, 1)',
                }]
            },
            options: {
                title: {
                    display: true,
                    text: "Location Graph"
                },
                legend: {
                    display: true,
                },

                showLines: false,
                scales: {
                    yAxes: [{
                        type: 'linear',
                        position: 'bottom',
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Y coordinate',
                            fontSize: 16
                        },
                        gridLines: {
                            display: true
                        },
                        ticks: {
                            beginAtZero:false,
                            fontSize: 14,
                        }
                    }],
                    xAxes: [{
                        type: 'linear',
                        position: 'bottom',
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'X coordinate',
                            fontSize: 16
                        },
                        gridLines: {
                            display: true
                        },
                        ticks: {
                            beginAtZero:false,
                            fontSize: 14,
                        }
                    }]
                },
                tooltips: {
                    displayColors: false,
                    callbacks: {
                        title: function(tooltipItems, data) {
                            let index = tooltipItems[0].index;
                            let datasetIndex = tooltipItems[0].datasetIndex;
                            let dataset = data.datasets[datasetIndex];
                            let datasetItem = dataset.data[index];

                            let clients = clientLocation[datasetItem.id];
                            return "ClientId: " + clients.id;
                        },
                        label: function(tooltipItems, data) {
                            let output = "";

                            let index = tooltipItems.index;
                            let datasetIndex = tooltipItems.datasetIndex;
                            let dataset = data.datasets[datasetIndex];
                            let datasetItem = dataset.data[index];

                            var clients = clientLocation[datasetItem.id];

                            output += "current location X: " + clients.x + "\n | \n";
                            output += "current location Y: " + clients.y + "\n | \n";
                            return output;
                        }
                    }
                }
            }
        });
    }



    $("#createQueue").click(function () {

        $.ajax({
            type: "GET",
            url: "/rabbitmq/messageReceiver/createQueue",
            dataType: "json",
            success: function (response) {
                $("#createRabbitMQQueue").text(response.message);
                console.log("succesfully created queues");
            },
            error: function () {
                $("#createRabbitMQMessage").text("something went wrong when creating queues");
                console.log("something went wrong when creating queues");
            }
        });

    });

    $("#send").click(function () {

        $.ajax({
            type: "GET",
            url: "/rabbitmq/messageSender/send",
            dataType: "json",
            success: function (response) {
                $("#sendRabbitMQMesssage").text(response.message);
                console.log("succesfully send message for rabbitMQ");
            },
            error: function () {
                $("#sendRabbitMQMesssage").text("something went wrong when creating queues");
                console.log("something went wrong when sending rabbitMQ messages.");
            }
        });

    });

    $("#getMessage").click(function () {

        $.ajax({
            type: "GET",
            url: "/rabbitmq/messageReceiver/getMsg",
            dataType: "json",
            success: function (response) {
                $("#getRabbitMQMessage").text(response.message);
                console.log(response.data);
                showMessageGraph(response);
                console.log("succesfully get messages for rabbitMQ");
            },
            error: function () {
                $("#createRabbitMQMessage").text("something went wrong when getting rabbitMQ messages.");
                console.log("something went wrong when getting rabbitMQ messages.");
            }
        });

    });

    $("#getResult").click(function () {

        $.ajax({
            type: "GET",
            url: "/rabbitmq/messageReceiver/getResult",
            dataType: "json",
            success: function (response) {
                $("#getRabbitMQResult").text(response.message);
                document.getElementById("result_table").style.display='block';
                createResultTable(response);
                console.log("succesfully get results for rabbitMQ");
            },
            error: function () {
                $("#createRabbitMQMessage").text("something went wrong when getting rabbitMQ results.");
                console.log("something went wrong when getting rabbitMQ results.");
            }
        });

    });

});

function showMessageGraph(response) {
    let high_array = [];
    let medium_array = [];
    let low_array = [];
    let total_array = [];

    let data = response.data;

    let totalLen = data.length;

    for (let i = 0; i < totalLen; i++) {
        let message = data[i];
        total_array.push({id: message['messageId'], x: i, y: message['receivedTime']});
        if (message.location === "Within 3 miles"){
            high_array.push({id: message['messageId'], x: i, y: message['receivedTime']});
        } else if (message.location === "3-10 miles away") {
            medium_array.push({id: message['messageId'], x: i, y: message['receivedTime']});
        } else if (message.location === "Further than 10 miles") {
            low_array.push({id: message['messageId'], x: i, y: message['receivedTime']});
        }
    }

    console.log("high array:", high_array);
    console.log("medium array:", medium_array);
    console.log("low array:", low_array);

    Chart.defaults.global.elements.point.radius = 1;
    Chart.defaults.global.elements.point.hitRadius = 20;
    Chart.defaults.global.elements.point.hoverBorderWidth = 4;

        let datasets = [];

        let dataset1 = {
            label: 'high priority messages',
            data: high_array,
            backgroundColor: 'rgba(255, 0, 0, 1)',
            borderColor: 'rgba(254, 100, 35, 1)',
            borderWidth: 1,
            fill: false,
        };

        let dataset2 = {
            label: 'medium priority messages',
            data: medium_array,
            backgroundColor: 'rgba(254, 162, 35, 0.35)',
            borderColor: 'rgba(254, 162, 35, 1)',
            borderWidth: 1,
            fill: false,
        };

        let dataset3 = {
            label: 'low priority messages',
            data: low_array,
            backgroundColor: 'rgba(75, 192, 192, 0.35)',
            borderColor: 'rgba(75, 192, 192, 1)',
            fill: false,
        };

        datasets.push(dataset1);
        datasets.push(dataset2);
        datasets.push(dataset3);

        let ctx = document.getElementById("message_graph").getContext('2d');
        let myLineChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: "Messages with Three Priorities",
                datasets: datasets,
            },
            options: {
                title: {
                    display: true,
                    text: "Messages with Three Priorities",
                },
                scales: {
                    yAxes: [{
                        type: 'linear',
                        position: 'bottom',
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Message Sending Time',
                            fontSize: 16
                        },
                        gridLines: {
                            display: true
                        },
                        ticks: {
                            beginAtZero:false,
                            fontSize: 14,
                        }
                    }],
                    xAxes: [{
                        type: 'linear',
                        position: 'bottom',
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'Messages by Sequence',
                            fontSize: 16
                        },
                        gridLines: {
                            display: true
                        },
                        ticks: {
                            beginAtZero:false,
                            fontSize: 14,
                        }
                    }]
                },
            }
        });
}





function createResultTable(response) {
    console.log("handleResult: populating result table from resultData");
    let resultTable = jQuery("#result_table_body");
    let result= response.data;

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + "high priority average time (ms)" + "</th>";
    rowHTML += "<th>" + result["0"] + "</th>";
    rowHTML += "</tr>";
    rowHTML += "<tr>";
    rowHTML += "<th>" + "medium priority average time (ms)" + "</th>";
    rowHTML += "<th>" + result["1"] + "</th>";
    rowHTML += "</tr>";
    rowHTML += "<tr>";
    rowHTML += "<th>" + "low priority average time (ms)" + "</th>";
    rowHTML += "<th>" + result["2"] + "</th>";
    rowHTML += "</tr>";
    resultTable.append(rowHTML);

    console.log("result table: ",resultTable);
}

function getLocArr(num,r,centerx,centery){
    let arr = [];
    let du = 360/num;

    for(let times=0;times<num;times++){
        let hudu = (2*Math.PI/360)*(du*times-180);
        let x = Math.round(centerx*1000)/1000+Math.round(Math.sin(hudu)*r*1000)/1000;
        let y = Math.round(centery*1000)/1000+Math.round(Math.cos(hudu)*r*1000)/1000;
        arr.push({id: -1, x:x, y: y})
    }
    return arr;
}


