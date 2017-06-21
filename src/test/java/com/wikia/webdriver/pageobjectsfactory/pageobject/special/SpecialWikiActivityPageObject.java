package com.wikia.webdriver.pageobjectsfactory.pageobject.special;

import com.wikia.webdriver.common.contentpatterns.URLsContent;
import com.wikia.webdriver.common.core.Assertion;
import com.wikia.webdriver.common.core.configuration.Configuration;
import com.wikia.webdriver.common.logging.PageObjectLogging;
import com.wikia.webdriver.pageobjectsfactory.componentobject.activity.Activity;
import com.wikia.webdriver.pageobjectsfactory.componentobject.activity.ActivityPageFactory;
import com.wikia.webdriver.pageobjectsfactory.componentobject.activity.creators.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecialWikiActivityPageObject extends SpecialPageObject {

  @FindBys(@FindBy(css = "li[class*=\"activity-type\"]"))
  private List<WebElement> activityWebElementList;
  @FindBys(@FindBy(css = "li.activity-ns-0"))
  private List<WebElement> activitiesList; //includes all of edit, new etc.
  @FindBys(@FindBy(css = "li.activity-type-edit"))
  private List<WebElement> editActivitiesList;
  @FindBys(@FindBy(css = "li.activity-type-new"))
  private List<WebElement> newPageActivitiesList;
  @FindBys(@FindBy(css = "li.activity-type-categorization"))
  private List<WebElement> categorizationActivitiesList;
  @FindBys(@FindBy(css = "li.activity-type-talk.activity-ns-1201"))
  private List<WebElement> wallPostActivitiesList;

  By entryTitleBy = By.cssSelector("strong a");
  By wallThreadAuthorBy = By.cssSelector(".wallfeed tr:first-child .real-name");
  By wallThreadMainContentBy = By.cssSelector(".wallfeed tr:first-child p:nth-child(2)");

  public SpecialWikiActivityPageObject(WebDriver driver) {
    super();
  }

  public SpecialWikiActivityPageObject open() {
    getUrl(urlBuilder.getUrlForWiki(Configuration.getWikiName())
           + URLsContent.SPECIAL_WIKI_ACTIVITY);

    return this;
  }

  /**
   * SUS-1309: Verify title, content and author of newly submitted Wall thread is visible on Wiki Activity
   *
   * @see com.wikia.webdriver.testcases.messagewall.MessageWallTests
   * @param postTitle title of new Wall Thread that is expected to be title of entry
   * @param postContent content of new Wall Thread that is expected to be shown in entry
   * @param authorName name of user who posted new thread who is expected to be author of entry
   */
  public void verifyNewWallThreadEntry(String postTitle, String postContent, String authorName) {
    // To prevent race conditions (something may have created new wall message while we were switching to WA)
    // we check all entries on WA which belong to wall posts
    WebElement result =
        wallPostActivitiesList
            .stream()
            .filter(
                postEntry ->
                    postEntry.findElement(entryTitleBy).getText().equals(postTitle) &&
                    postEntry.findElement(wallThreadAuthorBy).getText().equals(authorName) &&
                    postEntry.findElement(wallThreadMainContentBy).getText().startsWith(postContent)
            )
            .findFirst()
            .orElse(null);

    Assertion.assertNotNull(result);
  }

  public Boolean doesLastNRecentEditionsContains(int n, String articleName, String userName) {
    Boolean ifPassed = false;
    for (int i = 0; i < n; i++) {
      ifPassed = ifDetailsPresent(activitiesList.get(i), articleName, userName);
      break;
    }
//    PageObjectLogging.log("doesLastNRecentEditionsContains",
//            "WikiActivity module found recent new page: '" + articleName
//                    + "' that was created by user: " + userName, ifPassed);
    return ifPassed;
  }

  /**
   * verifies if there is the wanted NewPage on WikiActivity, searching through 5 recent editions.
   */
  public void verifyRecentNewPage(String articleName, String userName) {
    Boolean ifPassed = false;
    for (int i = 0; i < 4; i++) {
      ifPassed = ifDetailsPresent(newPageActivitiesList.get(i), articleName,
                                  userName);
      if (ifPassed) {
        PageObjectLogging.log("verifyRecentNewPage",
                              "WikiActivity module found recent new page: '" + articleName
                              + "' that was created by user: " + userName, true);
        break;
      }
    }
    if (!ifPassed) {
      PageObjectLogging.log("verifyRecentNewPage",
                            "new page:  '" + articleName + "' by user: '"
                            + userName + "', not found", false, driver);
    }

  }

  /**
   * verifies if there is the wanted new Blog on WikiActivity, searching through 5 recent editions.
   */
  public void verifyRecentNewBlogPage(String blogContent, String blogTitle, String userName) {
    Boolean ifPassed = false;
    for (int i = 0; i < 4; i++) {
      ifPassed = ifNewBlogDetailsPresent(newPageActivitiesList.get(i), blogContent, blogTitle,
                                         userName);
      if (ifPassed) {
        PageObjectLogging.log("verifyRecentNewBlogPage",
                              "WikiActivity module found recent new blog titled as: '" + blogTitle
                              + "' that was created by user: " + userName
                              + " and contains content: " + blogContent, true);
        break;
      }
    }
    if (!ifPassed) {
      PageObjectLogging.log("verifyRecentNewBlogPage",
                            "WikiActivity module didn't find recent new blog titled as: '"
                            + blogTitle
                            + "' that was created by user: " + userName
                            + " and containted content: " + blogContent, false, driver);
    }

  }

  /**
   * verifies if there is the wanted new Categorization on WikiActivity, searching through 5 recent
   * categorizations.
   */
  public void verifyRecentNewCategorization(String articleName,
                                            String userName) {
    Boolean ifPassed = false;
    for (int i = 0; i < 4; i++) {
      ifPassed = ifDetailsPresent(categorizationActivitiesList.get(i), articleName,
                                  userName);
      if (ifPassed) {
        PageObjectLogging.log("verifyRecentNewCategorization",
                              "WikiActivity module found recent new Categorization on artile: '"
                              + articleName
                              + "' that was done by user: " + userName, true);
        break;
      }
    }
    if (!ifPassed) {
      PageObjectLogging.log("verifyRecentNewCategorization",
                            "WikiActivity didn't find recent new Categorization on artile: '"
                            + articleName
                            + "' that was done by user: " + userName, false, driver);
    }

  }

  public void verifyNoRecentEditionIsPresent(String articleName, String userName){
    List<Activity> activityList = getActivities(10);
    Boolean listContainsRecentEdition = activityList.stream().anyMatch(
        p->p.getTitle().equals(articleName));
    Assertion.assertFalse(listContainsRecentEdition);
  }

  /**
   * Gets all activities from special page
   * @return
   */
  public List<Activity> getActivities() {
    return getActivities(activityWebElementList.size());
  }

  /**
   * Gets activities from special page, limited by provided value
   * @param numberOfActivities
   * @return
   */
  public List<Activity> getActivities(int numberOfActivities) {

    List<Activity> activityList = new ArrayList<>();
    //numberOfActivitiesToFetch set to max number of possible elements to fetch
    int numberOfActivitiesToFetch = numberOfActivities > activityWebElementList.size()
            ? activityWebElementList.size() : numberOfActivities;
    List<WebElement> activityWebElementSubList = activityWebElementList.subList(0, numberOfActivitiesToFetch);

    for (int i = 0; i < numberOfActivitiesToFetch - 1; i++) {
      Activity activity;
      List<ActivityPageCreator> creators = Arrays.asList(
              new EditActivityPageCreator(),
              new NewActivityPageCreator(),
              new CategorizationActivityPageCreator(),
              new WallPostActivityPageCreator()
      );
      activity = new ActivityPageFactory(driver, activityWebElementSubList.get(i), creators).createActivityPage();

      activityList.add(activity);
    }

    return activityList;
  }

  private Boolean ifDetailsPresent(WebElement webElement, String articleName,
                                   String userName) {
    boolean condition1 = webElement.findElement(By.cssSelector("strong a"))
        .getText().replaceAll("_", " ").contains(articleName);
    boolean condition2 = webElement.findElement(By.cssSelector("cite span a"))
        .getText().contains(userName);
    return (condition1 & condition2);
  }

  private Boolean ifNewBlogDetailsPresent(WebElement webElement,
                                          String blogContent, String blogTitle, String userName) {
    boolean condition1 = ifDetailsPresent(webElement, "blog:" + userName
                                                      + "/" + blogTitle, userName);
    boolean condition2 = webElement.findElement(By.cssSelector("td em"))
        .getText().contains("New blog");
    boolean condition3 = webElement
        .findElement(By.cssSelector("td:nth-of-type(2)")).getText()
        .contains(blogContent);
    return (condition1 & condition2 & condition3);
  }

}
