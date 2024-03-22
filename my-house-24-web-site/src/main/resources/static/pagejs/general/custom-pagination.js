
function buildPagination(response) {
    totalPages = response.totalPages;
    var pageNumber = response.pageable.pageNumber;
    page = pageNumber;
    if (totalPages > 1) {
        var prev = '';

        if (pageNumber !== 0) {
            prev = '<li class="page-item prev"><a class="page-link" id="prev" href="javascript:void(0);"><span aria-hidden="true">&laquo;</span></a></li>';
        } else {
            prev = '<li class="page-item prev disabled"><a class="page-link" href="javascript:void(0);"><span aria-hidden="true">&laquo;</span></a></li>';
        }


        var next = '';

        if (pageNumber !== totalPages - 1) {
            next = '<li class="page-item next"><a class="page-link" id="next" href="javascript:void(0);"><span aria-hidden="true">&raquo;</span></a></li>';
        } else {
            next = '<li class="page-item next disabled"><a class="page-link" href="javascript:void(0);"><span aria-hidden="true">&raquo;</span></a></li>';
        }

        var pages = '';
        var dots = '<li class="page-item disabled"><a class="page-link waves-effect" href="javascript:void(0);"> ... </a></li>';
        if (pageNumber < 3) {
            for (var i = 1; i <= 5; i++) {
                if (i <= totalPages) {
                    if (pageNumber + 1 !== i) {
                        pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + i + '</a></li>';
                    } else {
                        pages += '<li class="page-item active"><a class="page-link" href="javascript:void(0);">' + i + '</a></li>';
                    }
                }
            }
            if (totalPages == 6) {
                pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + 6 + '</a></li>';
            }
            if (totalPages > 6) {
                pages += dots;
                pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + totalPages + '</a></li>';
            }

        } else if (3 > totalPages - 1 - pageNumber) {
            pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">1</a></li>';
            if (totalPages > 6) {
                pages += dots
            }
            for (var i = totalPages - 4; i <= totalPages; i++) {
                if (i > 1) {
                    if (pageNumber + 1 !== i) {
                        pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + i + '</a></li>';
                    } else {
                        pages += '<li class="page-item active"><a class="page-link" href="javascript:void(0);">' + i + '</a></li>';
                    }
                }
            }

        } else {
            pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">1</a></li>';
            pages += dots

            pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + pageNumber + '</a></li>';
            pages += '<li class="page-item active"><a class="page-link" href="javascript:void(0);">' + (pageNumber + 1) + '</a></li>';
            pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + (pageNumber + 2) + '</a></li>';

            pages += dots;
            pages += '<li class="page-item"><a class="page-link" href="javascript:void(0);">' + totalPages + '</a></li>';
        }

        pages = prev + pages + next;
        $("ul.pagination").append(pages);
    }
}

$(document).on("click", "ul.pagination li a", function () {
    let val = $(this).text();
    let id = $(this).attr('id');
    if (id === "prev") {
        let activeValue = parseInt($("ul.pagination li.active").text());
        getServicePage(activeValue - 2);
    } else if (id === "next") {
        let activeValue = parseInt($("ul.pagination li.active").text());
        getServicePage(activeValue);
    } else {
        let currentPage = parseInt(val - 1);
        getServicePage(currentPage);
    }
});