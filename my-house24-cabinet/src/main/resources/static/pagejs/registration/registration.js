
$("#register-button").on("click", function () {
    clearAllErrorMessage();
    console.log(grecaptcha);
    if(grecaptcha.enterprise.getResponse()) {
        let formData = collectData();
        sendData(formData);
    } else {
        $(".g-recaptcha").parent().append(
            '<p class="error-message text-danger">Доведіть що ви не робот</p>'
        );
    }
});
function collectData() {
    let formData = new FormData();
    $("#formRegistration").find('input:text, input:password').each(function (){
        formData.append($(this).attr("name"), $(this).val());
    });
    formData.append("policy", $("#policy").is(':checked'));
    console.log(grecaptcha.enterprise.getResponse());
    formData.append("recaptcha", grecaptcha.enterprise.getResponse());
    return formData
}
function sendData(formData) {
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
            window.location.href = response;
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}
