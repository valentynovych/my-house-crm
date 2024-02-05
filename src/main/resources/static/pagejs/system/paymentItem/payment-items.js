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
            '     <button type="button" class="dropdown-item btn justify-content-start" data-bs-toggle="modal" data-bs-target="#modalCenter"' +
            '     onclick="addDeleteEvent(' + data.id + ')">\n' +
            '     <i class="ti ti-trash me-1"></i>Видалити\n' +
            '     </button>\n' +
            '     </div>\n' +
            '     </div>\n' +
            '</td>\n' +
            '</tr>').appendTo("tbody");
    }
    if (result.totalPages) {
        $('<div class="details-table d-flex gap-2 align-items-center"></div>').appendTo('.card-footer')

        const size = result.size;

        const from = page > 0 ? (page * size) + 1 : 1;
        const to = from + result.numberOfElements - 1;
        const total = result.totalElements;

        if (from === total) {
            $('<div class="dataTables_info"">' +
                'Показано ' + from + ' з ' + total + ' статтей' +
                '</div>').appendTo(".details-table");
        } else {
            $('<div class="dataTables_info">' +
                'Показано ' + from + '-' + to + ' з ' + total + ' статтей' +
                '</div>').appendTo(".details-table")
        }

        $('<label class="ms-3">Показати по: </label>' +
            '<div class="selecte-wrapper"><select name="tables_length" class="form-select form-select-sm">\n' +
            '      <option value="2">2</option>\n' +
            '      <option value="5">5</option>\n' +
            '      <option value="10">10</option>\n' +
            '       <option value="20">20</option>\n' +
            '</select> </div>').appendTo(".details-table");

        var $select = $('select[name="tables_length"]');
        $select.on("change", function () {
            tableLength = this.value;
            getPaymentItems(0);
        });
        $select.val(tableLength);

        drawPagination(result.totalPages, page, 'getPaymentItems');
    }
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}