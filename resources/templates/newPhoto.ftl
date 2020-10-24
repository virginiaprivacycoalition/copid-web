<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="container">
        <div class="row justify-content-center " style="height:100vh">
            <div class="col-8">
                <div class="card">
                    <div class="card-body">
                        <form action="${cop.url()}/photo/new" method="post" enctype="multipart/form-data" autocomplete="off">
                            <p>
                                Add cop photo
                            </p>
                            <div class="form-group">
                                <label>
                                    Cop
                                    <select disabled class="custom-select" id="cop" name="cop" required aria-required="true" aria-disabled="true">
                                        Select a cop to add the photo to

                                                <option value="${cop}" selected>${cop.formattedName}</option>

                                    </select>
                                </label>
                            </div>
                            <div class="form-group">
                                <label>
                                    Photo
                                    <input type="file" class="form-control" name="photo" required aria-required="true">
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