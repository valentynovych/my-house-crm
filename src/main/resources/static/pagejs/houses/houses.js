let tableLength = 5;
let currentPage = 0;
let byName = '';
let byAddress = '';
let timer;

$('#filter-by-name').on('input', function () {
    byName = this.value;
    delayBeforeSearch();
})
$('#filter-by-address').on('input', function () {
    byAddress = this.value;
    delayBeforeSearch();
})

$('.clear-filters').on('click', function () {
    $('#filter-by-name, #filter-by-address')
        .val('').trigger('input');
})

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getHouses(0)
    }, keyPause);
}

$(window).on("load", function () {
    getHouses(currentPage);
})

function getHouses(page) {
    blockCardDody();
    let url = new URL('houses/get-houses', window.location.origin + window.location.pathname);
    url.searchParams.append('page', page);
    url.searchParams.append('pageSize', tableLength);
    if (byName) {
        url.searchParams.append('name', byName);
    }
    if (byAddress) {
        url.searchParams.append('address', byAddress);
    }

    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
            currentPage = page;
            clearTableLine();
            $(".card-footer").children().remove();
            drawTable(result);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function drawTable(result) {
    const page = result.pageable.pageNumber;
    let iter = 0;
    for (const house of result.content) {

        $(`<tr data-href="houses/view-house/${house.id}" class="cursor-pointer">
            <td>${++iter}</td>
            <td>${house.name}</td>
            <td>${house.address}</td>
            <td class="text-center">
              <div class="dropdown">
               <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                       data-bs-toggle="dropdown">
                    <i class="ti ti-dots-vertical"></i>
               </button>
                 <div class="dropdown-menu">
                 <a class="dropdown-item" href="houses/edit-house/${house.id}">
                       <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                 </a>
                 <button type="button" class="dropdown-item btn justify-content-start" 
                        data-bs-toggle="modal" data-bs-target="#modalToDelete" onclick="addDeleteEvent(${house.id})">
                       <i class="ti ti-trash me-1"></i>${buttonLabelDelete}</button> 
                 </div>
              </div>
            </td>
            </tr>`
        ).appendTo("tbody");
        addListenerToRow();
    }

    drawPaginationElements(result, 'getHouses')
    drawPagination(result.totalPages, page, 'getHouses');
}

function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function addDeleteEvent(houseId) {
    $('.submit-delete').on('click', function () {
        if (houseId && houseId > 0) {

            $.ajax({
                type: 'delete',
                url: 'houses/delete/' + houseId,
                success: function () {
                    $('.close-modal').click()
                    toastr.success(successMessageOnDelete)
                    setTimeout(() => getHouses(currentPage), 400);
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