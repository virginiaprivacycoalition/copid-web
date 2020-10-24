<#import "template.ftl" as layout />
<@layout.mainLayout>
<div class="container">
    <div class="row justify-content-center align-items-center" style="height:100vh">
        <div class="col-4">
            <div class="card">
                <div class="card-body">
                    <form action="/login" method="post" autocomplete="off">
                        <p>
                            Log in as a trusted user
                        </p>
                        <div class="form-group">
                            <label>
                                Username
                                <input type="text" class="form-control" name="username">
                            </label>
                        </div>
                        <div class="form-group">
                            <label>
                                Password
                                <input type="password" class="form-control" name="password">
                            </label>
                        </div>
                        <button type="submit" id="sendlogin"  class="btn btn-primary">login</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</@layout.mainLayout>