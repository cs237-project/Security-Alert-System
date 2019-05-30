$(document).ready(function () {
        $("#addClient").click(function () {
            let clientNumber = $("#clientNumber").val();
            console.log("client number:"+ clientNumber);
            let addClientUrl = "/client/addClients/" + clientNumber;
            console.log("addClientUrl:"+ addClientUrl);


            $.ajax({
                type: "GET",
                url: addClientUrl,
                dataType: "text",
                success: function (response) {
                    $("#add_client_message").text(response);
                    console.log("succesfully added clients");
                },
                error: function () {
                    $("#add_client_message").text("something went wrong when adding clients");
                    console.log("something went wrong when adding clients");
                }
            });

        });
    });

function getClientResult(getClientsUrl) {
    $.ajax({
        type: "GET",
        url: getClientsUrl,
        dataType: "json",
        success: function (response) {
            console.log(response.message);
            handleClientResponse(response);
        },
        error: function () {
            $("#get_clients").text("something went wrong when trying to get clients");
            console.log("something went wrong when trying to get clients");
        }
    });
}

function handleClientResponse(response) {
    $("#get_clients").text(response.message+". We have "+response.data.length+" clients now.");
    const ctx = document.getElementById("client_locations").getContext('2d');
    const data = response.data;

    let clientLocation = [];
    let clientAddress = [];

    for (let i = 0; i < data.length; i++) {
        clientLocation.push({id: data[i]['clientId'], x:data[i]['locationx'], y: data[i]['locationy']});
        clientAddress.push({id: data[i]['clientId'], x:data[i]['addressx'], y: data[i]['addressy']});
    }

    var myChart = new Chart(ctx, {
        type: 'scatter',
        data: {
            label: 'current location and home location Dataset',
            datasets: [{
                data: clientLocation,
                label: 'client current location',
                backgroundColor: 'rgba(255, 0, 0, 1)',
            },
                {
                    data: clientAddress,
                    label: 'client home location',
                    backgroundColor: 'rgba(0, 0, 255, 1)'
                }]
        },
        options: {
            title: {
                display: true,
                text: "Client Current Location and Home Location Dataset"
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

                        let client = response.data[datasetItem.id];
                        return "ClientId: " + client.clientId;
                    },
                    label: function(tooltipItems, data) {
                        let output = "";

                        let index = tooltipItems.index;
                        let datasetIndex = tooltipItems.datasetIndex;
                        let dataset = data.datasets[datasetIndex];
                        let datasetItem = dataset.data[index];

                        var client = response.data[datasetItem.id];

                        output += "current location X: " + client.locationx + "\n | \n";
                        output += "current location Y: " + client.locationy + "\n | \n";
                        output += "home X: " + client.addressx + "\n | \n";
                        output += "home Y: " + client.addressy;
                        return output;
                    }
                }
            }
        }
    });
}