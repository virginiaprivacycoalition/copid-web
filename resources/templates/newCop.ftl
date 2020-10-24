<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="container">
        <div class="row justify-content-center align-items-center" style="height:100vh">
            <div class="col-4">
                <div class="card">
                    <div class="card-body">
                        <form action="/cop/new" method="post" enctype="multipart/form-data" autocomplete="off">
                            <p>
                                Add a new identified cop
                            </p>
                            <div class="form-group">
                                <label>
                                    First Name
                                    <input type="text" class="form-control" name="firstName" required aria-required="true">
                                </label>
                            </div>
                            <div class="form-group">
                                <label>
                                    Last Name
                                    <input type="text" class="form-control" name="lastName" required aria-required="true">
                                </label>
                            </div>
                            <div class="form-group">
                                <label>
                                    Title
                                    <input type="text" class="form-control" name="title">
                                </label>
                            </div>

                            <div class="form-group">
                                <label>
                                    Nick Name
                                    <input type="text" class="form-control" name="nickName">
                                </label>
                            </div>

                            <div class="form-group">
                                <label>
                                    Photo
                                    <input type="file" class="form-control" name="photo" required>
                                </label>
                            </div>
                            <button type="submit" id="addCop"  class="btn btn-primary">submit</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@layout.mainLayout>