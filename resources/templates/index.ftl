<#import "template.ftl" as layout />
<@layout.mainLayout>
    <style>
        .mainLink.figure:hover {
            min-height: 75vh;
            inset: 2%;
        }
        .mainLink.figure-img {
            max-height: 50vh;
            overflow: auto;
            align-content: space-evenly;
        }
        .mainLink.figure-img:hover {
            transform: scale(1.5, 1.5);
            max-height: 50vh;
            overflow: auto;
            align-content: space-evenly;
        }
    </style>
    <div class="container">
    <div class="row">
        <div class="col-sm">
            <#include "components/paginator.ftl">
        </div>
    </div>
    <div class="row">
    </div>



        <div class="container">
    <ul class="list-group justify-content-center">
        <div class="row">
        <#list cops as cop>
            <#if cop_index % 2 == 0>
                </div>
        <div class="row">

        </#if>
            <div class="col">
            <a href="${cop.url()}">
            <figure class="figure mainLink">
                <img src="${cop.photoUrl(cop.photos[0])}" class="figure-img img-fluid rounded mx-auto mainLink"
                     alt="Richmond Police Officer <strong>${cop.formattedName}</strong>">
                <figcaption class="figure-caption">Richmond Police Officer <strong>${cop.formattedName}</strong>
                </figcaption>

                            <#if sesh?? || user??>
                                <form action="/api/cop/${cop.url()}" method="post">
                                    <button type="submit" class="btn btn-primary">
                                    DELETE
                                    </button>
                                </form>
</#if>
            </figure>
                </a>
            </div>
        </#list>
    </ul>
    </div>
        <div class="row justify-content-center">
            <form class="form-inline ">
                <label for="copsPerPage">Items per page
                </label>
                <select class="form-control" name="copsPerPage" id="copsPerPage">
                    <option selected>4</option>
                    <option>10</option>
                    <option>20</option>
                    <option>50</option>
                </select>
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit" formmethod="post" formaction="/cops/${page}">Go</button>
            </form>
        </div>
    </div>
<#--    <div class="container">-->
<#--        <div class="row">-->
<#--            <a href="/employee?action=new" class="btn btn-secondary float-right" role="button">New Employee</a>-->
<#--        </div>-->
<#--    </div>-->
</@layout.mainLayout>