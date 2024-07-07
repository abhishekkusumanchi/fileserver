<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>File Explorer</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <h1 id="currentPath">File Explorer</h1>
    <form id="uploadForm" enctype="multipart/form-data">
        <input type="file" name="file" /> 
        <input type="text" name="path" id="path" /> 
        <input type="submit" value="Upload" onclick="uploadFile()" />
    </form>
    <hr>
    <form id="createFolderForm">
        <label for="folderName">Folder Name:</label> <input type="text"
            id="folderName" name="folderName" required> <input
            type="submit" value="Create Folder">
    </form>
    <hr>
    <button id="btnPrevious">Previous</button>
    <div id="fileExplorer">
        <ul id="fileList">
        </ul>
    </div>
    <script>
        var currentFolderPath = "E:/SFE"; // Initial folder path

        $(document).ready(function() {
            $("#path").val(currentFolderPath);
            $("#path").hide();
            openFolder(currentFolderPath); // Initial call to list the root folder
        });

        $("#btnPrevious").click(function() {
            var lastIndex = currentFolderPath.lastIndexOf("/");
            if (lastIndex !== -1) {
                currentFolderPath = currentFolderPath.substring(0, lastIndex);
                openFolder(currentFolderPath);
                updateCurrentPath(currentFolderPath);
            }
        });

        function updateCurrentPath(path) {
            $("#currentPath").text("File Explorer: " + path);
            $("#path").val(path);
        }

        function openFolder(folderPath) {
            currentFolderPath = folderPath;
            $("#path").val(currentFolderPath);
            $.ajax({
                url: "/file/list",
                type: "GET",
                data: {
                    folderPath: folderPath
                },
                dataType: "json",
                success: function(data) {
                    $("#fileList").empty();
                    data.forEach(function(item) {
                        var li = $("<li>");
                        if (item.type === "folder") {
                            li.html("<button onclick=\"openFolder('" + item.path + "')\">" + item.name + "</button>");
                        } else {
                            li.html("<a href='/file/download?filePath=" + item.path + "'>" + item.name + "</a>");
                        }
                        $("#fileList").append(li);
                    });
                    updateCurrentPath(folderPath);
                },
                error: function(xhr, status, error) {
                    alert("Error: " + error);
                }
            });
        }

        $("#createFolderForm").submit(function(event) {
            event.preventDefault();

            var folderName = $("#folderName").val();

            $.ajax({
                url: "/file/createFolder",
                type: "POST",
                data: {
                    folderName: folderName,
                    folderPath: currentFolderPath
                },
                success: function(response) {
                    alert(response.message);
                    openFolder(currentFolderPath);
                },
                error: function(xhr, status, error) {
                    alert("Error creating folder: " + error);
                }
            });
        });

        function uploadFile() {
            var formData = new FormData(document.getElementById("uploadForm"));

            $.ajax({
                url: "/file/uploadFile", 
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    alert(response.message);
                    openFolder(currentFolderPath);
                },
                error: function(xhr, status, error) {
                    alert("Error uploading file: " + error);
                }
            });
        }
    </script>
</body>
</html>
