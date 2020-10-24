<#import "template.ftl" as layout />
<@layout.mainLayout>
    <div class="container-fluid">
        <#list messages as message>
            <div class="row">
            <div class="alert alert-success" role="article">
                <h4 class="alert-heading">${message.timePosted()}</h4>
                <p>${message.content}</p>
                <hr>
                <p class="mb-0">;-D</p>
            </div>
            </div>
        </#list>
    </div>



</@layout.mainLayout>