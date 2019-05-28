$(document).ready(function () {
    $("#createMessage").click(function () {
        let emergencyType = $('#securityIssue option:selected').text();
        console.log("emergencyType:"+ emergencyType);


        if (emergencyType === "Please select") {
            $("#createRabbitMQMessage").text("You need to input a valid emergency type.");
        } else {
            let url = "/rabbitmq/messageSender/create/" + emergencyType;
            console.log(url);
            $.ajax({
                type: "GET",
                url: url,
                dataType: "json",
                success: function (response) {
                    $("#createRabbitMQMessage").text(response.message);
                    console.log("succesfully added clients");
                },
                error: function () {
                    $("#createRabbitMQMessage").text("something went wrong when creating messages");
                    console.log("something went wrong when creating messages");
                }
            });
        }

    });



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
                $("#createRabbitMQQueue").text(response.message);
                console.log("succesfully send message for rabbitMQ");
            },
            error: function () {
                $("#createRabbitMQMessage").text("something went wrong when creating queues");
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
                createMessageTable(response);
                console.log("succesfully get messages for rabbitMQ");
            },
            error: function () {
                $("#createRabbitMQMessage").text("something went wrong when getting rabbitMQ messages.");
                console.log("something went wrong when getting rabbitMQ messages.");
            }
        });

    });

});

function createMessageTable(response) {
    console.log("handleResult: populating message table from resultData");
    let messageTable = jQuery("#message_table_body");
    let result=response.data;

    for (let i = 0; i < result.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + result[i]["messageId"] + "</th>";
        rowHTML += "<th>" + result[i]["type"] + "</th>";
        rowHTML += "<th>" + result[i]["location"] + "</th>";
        rowHTML += "<th>" + result[i]["happenTime"] + "</th>";
        rowHTML += "<th>" + result[i]["receivedTime"] + "</th>";
        rowHTML += "</tr>";
        messageTable.append(rowHTML);
    }
    console.log("message table: ",messageTable);
}


