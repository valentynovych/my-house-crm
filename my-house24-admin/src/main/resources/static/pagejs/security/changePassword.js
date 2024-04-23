function send() {
    clearAllErrorMessage();
    trimInputsValue();
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
        error: function (error) {
            printErrorMessageToField(error);
            if (error.status == 403) {
                window.location.href = 'tokenExpired';
            }
        }
    });
}