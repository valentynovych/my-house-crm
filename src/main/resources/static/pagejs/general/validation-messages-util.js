function printErrorMessageToField(errorResponse) {
    console.log(errorResponse.responseJSON);
    if (errorResponse.status === 400) {
        const errorMap = new Map(Object.entries((errorResponse.responseJSON)));
        errorMap.forEach((value, key) => {
            $("#" + key).addClass("is-invalid").parent().append($(
                '<p class="error-message invalid-feedback m-0">' + value + '</p>'
            ));
        })
    }
}

function clearAllErrorMessage() {
    $('.error-message').remove();
    $('.is-invalid').removeClass('is-invalid');
}