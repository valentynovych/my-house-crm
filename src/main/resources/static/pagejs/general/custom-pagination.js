function drawPagination(countPages, page, getMethod, classToAdd) {

    if (countPages <= 1) {
        return;
    }

    let paginationList = $(`<ul class="pagination pagination-sm"></ul>`);
    let previous = $(`<li class="page-item prev">
                            <a class="page-link waves-effect" onclick="${getMethod}(${page - 1})">
                                <i class="ti ti-chevron-left tf-icon fs-6"></i>
                            </a>
                       </li>`);
    let next = $(`<li class="page-item next">
                            <a class="page-link waves-effect" onclick="${getMethod}(${page + 1})">
                                <i class="ti ti-chevron-right tf-icon fs-6"></i>
                            </a>
                      </li>`);
    previous.appendTo(paginationList);

    if (countPages <= 5) {
        for (let elemIndex = 0; elemIndex < countPages; elemIndex++) {
            middleItem(paginationList, elemIndex, getMethod);
        }
    }
    if (page < 4 && countPages > 5) {
        for (let elemIndex = 0; elemIndex < countPages; elemIndex++) {
            if (elemIndex < 5) {
                middleItem(paginationList, elemIndex, getMethod);
            } else if (elemIndex === 5) {
                createDotsItem(paginationList)
                lastItem(paginationList, getMethod)
                break;
            }
        }
    }

    if (page >= 4 && page < countPages - 4) {
        firstItem(paginationList, getMethod)
        createDotsItem(paginationList)
        for (let elemIndex = page - 1; elemIndex < page + 2; elemIndex++) {
            middleItem(paginationList, elemIndex, getMethod)
        }
        createDotsItem(paginationList)
        lastItem(paginationList, getMethod);
    }

    if (page > 4 && page >= countPages - 4) {
        firstItem(paginationList, getMethod)
        createDotsItem(paginationList);
        for (let elemIndex = countPages - 5; elemIndex < countPages; elemIndex++) {
            middleItem(paginationList, elemIndex, getMethod)
        }
    }

    next.appendTo(paginationList);

    if (page === 0) {
        previous.addClass('disabled');
    }
    if (page === countPages - 1) {
        next.addClass('disabled');
    }

    paginationList.appendTo(`.${classToAdd ? classToAdd : 'card-footer'}`);

    function firstItem(paginationList, method) {
        let firstItem = $(`<li class="page-item first"></li>`);
        firstItem.attr('onclick', `${method}(0)`);
        firstItem.html(`<a class="page-link waves-effect">1</a>`);
        firstItem.appendTo(paginationList);
    }

    function middleItem(paginationList, elemIndex, method) {
        let oneItem = $(`<li class="page-item"></li>`);
        oneItem.attr('onclick', `${method}(${elemIndex})`);
        oneItem.html(`<a class="page-link waves-effect">${elemIndex + 1}</a>`)
        if (page === elemIndex) {
            oneItem.addClass('active');
        }
        oneItem.appendTo(paginationList);

    }

    function lastItem(paginationList, method) {
        let lastItem = $(`<li class="page-item last"></li>`);
        lastItem.attr('onclick', `${method}(${countPages - 1})`);
        lastItem.html(`<a class="page-link waves-effect">${countPages}</a>`);
        lastItem.appendTo(paginationList);
    }

    function createDotsItem(paginationList) {
        let dots = $(`<li class="page-item"><span class="page-link waves-effect">...</span></li>`);
        dots.appendTo(paginationList);
    }
}