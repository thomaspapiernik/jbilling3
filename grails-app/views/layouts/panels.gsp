%{--
  JBILLING CONFIDENTIAL
  _____________________

  [2003] - [2012] Enterprise jBilling Software Ltd.
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Enterprise jBilling Software.
  The intellectual and technical concepts contained
  herein are proprietary to Enterprise jBilling Software
  and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden.
  --}%

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <g:render template="/layouts/includes/head"/>
    <g:javascript library="panels"/>

    <script type="text/javascript">
        function renderRecentItems() {
            $.ajax({
                url: "${resource(dir:'')}/recentItem",
                global: false,
                success: function(data) { $("#recent-items").replaceWith(data) }
            });
        }

        $(document).ajaxSuccess(function(e, xhr, settings) {
            renderRecentItems();
        });
    </script>

    <g:layoutHead/>
</head>
<body>
<div id="wrapper">
    <g:render template="/layouts/includes/header"/>

    <div id="main">
        <g:render template="/layouts/includes/breadcrumbs"/>

        <div id="left-column">
            <!-- filters -->
            <g:if test="${filters}">
                <g:set var="target" value="${filterRender ?: 'first'}"/>
                <g:set var="action" value="${filterAction ?: 'list'}"/>

                <g:formRemote name="filters-form" url="[action: action]" onSuccess="render(data, ${target});">
                    <g:hiddenField name="applyFilter" value="true"/>
                    <g:render template="/layouts/includes/filters" model="[filters: filters, filterRender: filterRender, filterAction: filterAction]"/>
                </g:formRemote>

                <g:render template="/layouts/includes/filterSaveDialog"/>
            </g:if>

            <!-- shortcuts -->
            <g:if test="${session['shortcuts']}">
                <g:render template="/layouts/includes/shortcuts"/>
                <inc:include controller="shortcut" action="index"/>
            </g:if>

            <!-- recently viewed items -->
            <g:render template="/layouts/includes/recent"/>
        </div>


        <!-- content columns -->
        <div class="columns-holder">
            <g:render template="/layouts/includes/messages"/>
            <g:render template="/layouts/includes/errors"/>

            <!-- viewport of visible columns -->
            <div id="viewport">
                <div class="column panel">
                    <div id="column1" class="column-hold">
                        <g:pageProperty name="page.column1"/>
                    </div>
                </div>

                <div class="column panel">
                    <div id="column2" class="column-hold">
                        <g:pageProperty name="page.column2"/>
                    </div>
                </div>
            </div>

            <!-- template for new column-->
            <div id="panel-template" class="column panel">
                <div class="column-hold"></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
