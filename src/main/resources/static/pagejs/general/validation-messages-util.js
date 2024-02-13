function printErrorMessageToField(errorResponse) {
    console.log(errorResponse.responseJSON);
    if (errorResponse.status === 400) {
        const errorMap = new Map(Object.entries((errorResponse.responseJSON)));
        errorMap.forEach((value, key) => {
            const input = $('[name="' + key + '"]');
            if (!input.attr('disabled')) {
                input.addClass("is-invalid").parent().append($(
                    '<p class="error-message invalid-feedback m-0">' + value + '</p>'));
                input.on('change', function () {
                    $(this).removeClass('is-invalid');
                    $(this).closest('.invalid-feedback').remove();
                });
            }
        })
    }
}

function clearAllErrorMessage() {
    $('.error-message').remove();
    $('.is-invalid').removeClass('is-invalid');
}

function maxInputLength(input, maxLength) {
    var val = $(input).val();
    if (val.length > maxLength) {
        $(input).val(val.substring(0, maxLength));
    }
}