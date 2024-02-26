let tableLength = 5;
let currentPage = 0;

$(window).on("load", function () {
    getPaymentItems(currentPage);
})

function getPaymentItems(page) {
    blockCardDody();
    var getUrl = 'payment-items/get-items?page=' + page + '&pageSize=' + tableLength;

    $.ajax({
        type: 'get',
        url: getUrl,
        dataType: 'json',
        success: function (result) {
            currentPage = page;
            clearTableLine();
            $(".card-footer").children().remove();
            drawTable(result);
        },
        error: function () {
            toastr.error("Упс.. Виникла помилка");
        }
    });
}

function drawTable(result) {
    if (result.content && result.content.length > 0) {
        const page = result.pageable.pageNumber;
        for (const data of result.content) {

            const badge = data.paymentType === 'INCOME'
                ? '<span class="badge bg-label-success me-1">Дохід</span>'
                : '<span class="badge bg-label-danger me-1">Витрати</span>';

            $('<tr>\n' +
                '<td>' + data.name + '</td>\n' +
                '<td>' + badge + '</td>\n' +
                '<td>\n' +
                '  <div class="dropdown">\n' +
                '   <button type="button" class="btn p-0 dropdown-toggle hide-arrow"\n' +
                '           data-bs-toggle="dropdown">\n' +
                '    <i class="ti ti-dots-vertical"></i>\n' +
                '     </button>\n' +
                '     <div class="dropdown-menu">\n' +
                '     <a class="dropdown-item" href="payment-items/edit-item/' + data.id + '">\n' +
                '     <i class="ti ti-pencil me-1"></i>Редагувати\n' +
                '     </a>\n' +
                '     <button type="button" class="dropdown-item btn justify-content-start" data-bs-toggle="modal" data-bs-target="#modalToDelete"' +
                '     onclick="addDeleteEvent(' + data.id + ')">\n' +
                '     <i class="ti ti-trash me-1"></i>Видалити\n' +
                '     </button>\n' +
                '     </div>\n' +
                '     </div>\n' +
                '</td>\n' +
                '</tr>').appendTo("tbody");
        }

        drawPaginationElements(result, 'getPaymentItems');
        drawPagination(result.totalPages, page, 'getPaymentItems');
    } else {
        $(`<tr>
            <td colspan="4" class="text-center fw-bold h4">${dataNotFound}</td>
           </tr>`).appendTo('tbody');
    }

}

function addDeleteEvent(paymentItemId) {
    $('.submit-delete').on('click', function () {
        if (paymentItemId && paymentItemId > 0) {

            $.ajax({
                type: 'delete',
                url: 'payment-items/delete/' + paymentItemId,
                success: function () {
                    $('.close-modal').click()
                    toastr.success(successMessageOnDelete)
                    setTimeout(() => getPaymentItems(currentPage), 400);
                },
                error: function () {
                    $('.close-modal').click()
                    toastr.error(errorMessageOnDelete);

                }
            })
        }
    })
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}