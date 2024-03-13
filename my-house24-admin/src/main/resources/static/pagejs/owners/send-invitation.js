
$("#invite-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: {
            email: $("#email").val()
        },
        success: function (response) {
            toastr.success(sentMessage);
            window.location.href = response;
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
});