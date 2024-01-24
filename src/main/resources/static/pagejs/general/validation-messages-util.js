
function printErrorMessageToField(errorResponse) {

    if (errorResponse.status === 400) {
        for (let fieldError of errorResponse.responseJSON) {
            $("#" + fieldError.field).addClass("is-invalid").parent().append($(
                '<p class="error-message invalid-feedback m-0">' + fieldError.defaultMessage + '</p>'
            ));
        }
    }
}

function clearAllErrorMessage() {
    $('.error-message').remove();
    $('.is-invalid').removeClass('is-invalid');
}