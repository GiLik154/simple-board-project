var selectedPostId = null;

function updateSelectedPostId(checkbox) {
    var postId = checkbox.value;
    var checkboxes = document.getElementsByName("selectedPosts");

    for (var i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i] !== checkbox) {
            checkboxes[i].checked = false;
        }
    }

    selectedPostId = checkbox.checked ? postId : null;
}

function navigateToCreatePage() {
    window.location.href = "create";
}

function navigateToUpdatePage() {
    if (selectedPostId) {
        window.location.href = "/update?postId=" + selectedPostId;
    } else {
        alert("수정할 게시물을 선택해주세요.");
    }
}

function navigateToDeletePage() {
    if (selectedPostId) {
        var windowFeatures = "height=400,width=600,resizable=yes"; // 작은 윈도우 창 크기 및 설정
        window.open("/delete?postId=" + selectedPostId, "_blank", windowFeatures);
    } else {
        alert("삭제할 게시물을 선택해주세요.");
    }
}