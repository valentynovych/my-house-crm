let tableLength = 5;
$(document).ready(function () {
    getServicePage(0);
});

function getServicePage(currentPage) {
    blockBy('.content-wrapper');
    $.ajax({
        type: "GET",
        url: "services/get",
        data : {
            page: currentPage,
            pageSize: tableLength
        },
        success: function (response) {
            console.log(response);
            showPage(response);
            unblockBy('.content-wrapper');
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function showPage(response) {
    for(let service of response.content){
        $("#services").append(
            `<div class="d-flex justify-content-between mt-5">
                <div class="col-7">
                    <img style="max-width: 100%" src="${"../uploads/"+service.image}">
                </div>
                <div class="col-4">
                    <h4>${service.title}</h4>
                    <div>
                        ${service.description}
                    </div>
                </div>
            </div>`
        );
    }
    // if (response.totalPages > 0) {
    //     const page = response.pageable.pageNumber;
    //     drawPaginationElements(response, "getServicePage");
    //     drawPagination(response.totalPages, page, 'getServicePage');
    // }
}
