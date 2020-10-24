<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="container">
                <div class="card">
                    <div class="card-body">
                        <form action="/message" method="post" autocomplete="off">
                            <p>
                                Create new post
                            </p>
                            <div class="form-group">
                                <label>
                                    Enter message
                                    <textarea class="form-control" name="content" id="content"></textarea>
                                </label>
                            </div>
                            <button type="submit" id="submit"  class="btn btn-primary">post</button>
                        </form>
                    </div>
        </div>
    </div>
</@layout.mainLayout>