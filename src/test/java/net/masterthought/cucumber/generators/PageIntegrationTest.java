package net.masterthought.cucumber.generators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.masterthought.cucumber.generators.helpers.BuildInfoAssertion;
import net.masterthought.cucumber.generators.helpers.DocumentAssertion;
import net.masterthought.cucumber.generators.helpers.LinkAssertion;
import net.masterthought.cucumber.generators.helpers.NavigationAssertion;
import net.masterthought.cucumber.generators.helpers.NavigationItemAssertion;
import net.masterthought.cucumber.generators.helpers.TableRowAssertion;
import net.masterthought.cucumber.generators.helpers.WebAssertion;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class PageIntegrationTest extends Page {

    @Test
    public void generatePage_onDefaultConfiguration_generatesDefaultItemsInNaviBarfor() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new FeaturesOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        NavigationAssertion navigation = document.getNavigation();
        NavigationItemAssertion[] menuItems = navigation.getNaviBarLinks();

        navigation.hasPluginName();
        assertThat(menuItems).hasSize(3);

        menuItems[0].hasLinkToFeatures();
        menuItems[1].hasLinkToTags();
        menuItems[2].hasLinkToSteps();
    }

    @Test
    public void generatePage_onJenkinsConfiguration_generatesAllItemsInNaviBarfor() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setRunWithJenkins(true);
        configuration.setBuildNumber("123");

        page = new TagReportPage(reportResult, configuration, reportResult.getAllTags().get(0));

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        NavigationAssertion navigation = document.getNavigation();
        NavigationItemAssertion[] menuItems = navigation.getNaviBarLinks();

        navigation.hasPluginName();
        assertThat(navigation.getNaviBarLinks()).hasSize(6);

        menuItems[0].hasLinkToJenkins(configuration);
        menuItems[1].hasLinkToPreviousResult(configuration, page.getWebPage());
        menuItems[2].hasLinkToLastResult(configuration, page.getWebPage());

        menuItems[3].hasLinkToFeatures();
        menuItems[4].hasLinkToTags();
        menuItems[5].hasLinkToSteps();
    }

    @Test
    public void generatePage_onDefaultConfiguration_generatesSummaryTable() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new StepsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        BuildInfoAssertion buildInfo = document.getBuildInfo();

        TableRowAssertion headValues = buildInfo.getHeaderRow();
        headValues.hasExactValues("Project", "Date");

        assertThat(buildInfo.getProjectName()).isEqualTo(configuration.getProjectName());
        buildInfo.hasBuildDate(false);
    }

    @Test
    public void generatePage_onJenkinsConfiguration_generatesSummaryTableWithBuildNumber() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setRunWithJenkins(true);
        configuration.setBuildNumber("123");

        page = new StepsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        BuildInfoAssertion buildInfo = document.getBuildInfo();

        TableRowAssertion headValues = buildInfo.getHeaderRow();
        headValues.hasExactValues("Project", "Number", "Date");

        assertThat(buildInfo.getProjectName()).isEqualTo(configuration.getProjectName());
        assertThat(buildInfo.getBuildNumber()).isEqualTo(configuration.getBuildNumber());
        buildInfo.hasBuildDate(true);
    }

    @Test
    public void generatePage_generatesFooter() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new TagReportPage(reportResult, configuration, reportResult.getAllTags().get(0));

        // when
        page.generatePage();

        // then
        DocumentAssertion document = documentFrom(page.getWebPage());
        WebAssertion footer = extractFooter(document);
        LinkAssertion[] footerLinks = extractFooterLinks(footer);

        assertThat(footerLinks).hasSize(2);
        footerLinks[0].hasLabelAndAddress("Jenkins Plugin", "https://github.com/jenkinsci/cucumber-reports-plugin");
        footerLinks[1].hasLabelAndAddress("Cucumber-JVM Reports", "https://github.com/damianszczepanik/cucumber-reporting");
    }

    private WebAssertion extractFooter(WebAssertion document) {
        return document.byId("footer", WebAssertion.class);
    }

    private LinkAssertion[] extractFooterLinks(WebAssertion footer) {
        return footer.allBySelector("a", LinkAssertion.class);
    }

}
