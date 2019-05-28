
$(document).ready(function () {
    $("#createMessage").click(function () {
        let emergencyType = $('#SecurityIssue :selected').text();
        console.log("emergencyType:"+ emergencyType);


        if (emergencyType === "Choose a Emergency Type") {
            $("#createRabbitMQMessage").text("You need to input a valid emergency type.");
        } else {
            let url = "/rabbitmq/messageSender/create/" + emergencyType;
            $.ajax({
                type: "GET",
                url: url,
                dataType: "json",
                success: function (response) {
                    $("#createRabbitMQMessage").text(response.message);
                    console.log("succesfully added clients");
                },
                error: function () {
                    $("#createRabbitMQMessage").text("something went wrong when creating queues");
                    console.log("something went wrong when creating queues");
                }
            });
        }

    });
});




