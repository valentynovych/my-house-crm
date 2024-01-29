function send() {
    clearAllErrorMessage();
    $.ajax({
        type: "POST",
        url: "changePassword",
        data: {
            token: token,
            password: $("#password").val(),
            confirmPassword: $("#confirmPassword").val()
        },
        success: function () {
            window.location.href = 'success';
        },
        error: function (XMLHttpRequest) {
            printErrorMessageToField(error);
            if (XMLHttpRequest.status == 403) {
                window.location.href = 'tokenExpired';
            }
        }
    });
}