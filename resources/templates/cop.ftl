<#import "template.ftl" as layout />
<@layout.mainLayout>

<div class="container">
    <#if user??>


        <a href="${cop.url()}/photo/new" class="button"><h4>Add Photo</h4></a>

    <form action="${cop.url()}/photo/new" method="post" autocomplete="off">
        <input type="hidden" class="serializedFormContainer" value="${user}">
    </form>
        </#if>


</div>
<#list cop.photos as photo >
    <figure class="figure">
        <img src="${cop.photoUrl(photo)}" class="figure-img img-fluid rounded mx-auto"
             alt="A bully">
        <#if user?? >
            <figcaption class="figure-caption">
                <form action="${cop.photoUrl(photo)}/delete" method="post" autocomplete="off">
                    <input type="hidden" class="serializedFormContainer" value="${user}">
                    <button type="submit" id="sendDelete"  class="btn btn-primary">Delete (cannot be undone)</button>
                </form>

            </figcaption>
        </#if>
    </figure>
</#list>


</@layout.mainLayout>