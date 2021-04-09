package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.MenuItem;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.repository.IPageRepository;
import be.fcip.cms.persistence.service.IWebsiteService;
import be.fcip.cms.util.CmsStringUtils;
import be.fcip.cms.util.CmsUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(classes= CacheablePageTreeProviderImpl.class)
public class CacheablePageTreeProviderTest {

    @MockBean private IPageRepository pageRepository;
    @MockBean private ICacheablePageProvider pageProvider;
    @MockBean private IWebsiteService websiteService;

    @InjectMocks private CacheablePageTreeProviderImpl cacheablePageTreeProvider;

    public List<PageEntity> roots;

    /**
     *  Create tree structure et setup mockito
     *   p1 (folder)
     *   | ---- c1 (page)
     *   | ---- c2 (link)
     *   | ---- c3 (folder)
     *   p2 (page)
     *   f1,f2,f3,f4 (page should not displayed in the menu)
     */
    @BeforeEach
    public void setupTree(){
        roots = new ArrayList<>();
        PageEntity p1, p2, c1, c2, c3, f1, f2, f3, f4;
        p1 = createPage(1L, "fr", "Page 1", "Menu Page 1", CmsUtils.TEMPLATE_FOLDER_ID ,null);
        c1 = createPage(2L, "fr", "Child 1", "Menu Child 1",  1L, p1);
        c2 = createPage(3L, "fr", "Child 2", "Menu Child 2",  CmsUtils.TEMPLATE_LINK_ID, p1);
        c3 = createPage(9L, "fr", "Child 3", "Menu Child 3",  CmsUtils.TEMPLATE_FOLDER_ID, p1);
        p1.setPageChildren(Arrays.asList(c1, c2, c3));

        p2 = createPage(4L, "fr", "Page 2", "Menu Page 2", 1L, null);

        f1 = createPage(5L, "fr", "Bad 1", "Menu Bad 1", 1L,null);
        f1.setEnabled(false);
        f2 = createPage(6L, "fr", "Bad 2", "Menu Bad 2", 1L,null);
        f2.setMenuItem(false);
        f3 = createPage(7L, "fr", "Bad 3", "Menu Bad 3", 1L,null);
        PageContentEntity f3Content = f3.getContentMap().get("fr");
        f3Content.setEnabled(false);
        f4 = createPage(8L, "fr", "Bad 4", "Menu Bad 4", CmsUtils.TEMPLATE_FOLDER_ID,null);

        roots.add(p1);roots.add(p2);roots.add(f1);roots.add(f2);roots.add(f3); roots.add(f4);
        Mockito.when(pageRepository.findRootsByPageIdCustom(null, "fr", true, 1L)).thenReturn(roots);
    }

    @Test
    void returnFirstLevelPage_getMenuItemTest(){
        List<MenuItem> result = cacheablePageTreeProvider.getMenuItem(null, "fr", 0, 1L, false, null, null, 1L);
        assertNotNull(result);
        assertThat(result, hasSize(1));
    }

    @Test
    void returnTheNameOfPage_getMenuItemTest(){
        List<MenuItem> result = cacheablePageTreeProvider.getMenuItem(null, "fr", 0, 1L, false, null, null, 1L);
        assertEquals(result.get(0).getTitle(), "Menu Page 2");
    }

    @Test
    void onlyTitleShouldNotDisplayMenuName_getMenuItemTest(){
        List<MenuItem> result = cacheablePageTreeProvider.getMenuItem(null, "fr", 0, 1L, true, null, null, 1L);
        assertEquals(result.get(0).getTitle(), "Page 2");
    }

    @Test
    void notReturnFolderWithoutChildren_getMenuItemTest(){
        List<MenuItem> result = cacheablePageTreeProvider.getMenuItem(null, "fr", 0, 1L, false, null, null, 1L);
        assertThat(result, hasSize(1));
    }

    @Test
    void returnFolderWithChildren_getMenuItemTest(){
        List<MenuItem>  result = cacheablePageTreeProvider.getMenuItem(null, "fr", 1, 1L, false, null, null, 1L);
        assertThat(result, hasSize(2));
    }

    @Test
    void currentPageShouldBeActive_getMenuItemTest(){
        List<MenuItem>  result = cacheablePageTreeProvider.getMenuItem(null, "fr", 1, 1L, false, null, null, 1L);
        assertTrue(result.get(0).isActive());
    }

    @Test
    void childrenIsPresent_getMenuItemTest(){
        List<MenuItem>  result = cacheablePageTreeProvider.getMenuItem(null, "fr", 1, 1L, false, null, null, 1L);
        assertNotNull(result.get(0).getChildren());
    }


    @Test
    void typeShouldBeSetted_getMenuItem(){
        List<MenuItem>  result = cacheablePageTreeProvider.getMenuItem(null, "fr", 1, null, true, null, 1, 1L);
        List<MenuItem> children = result.get(0).getChildren();
        assertEquals(children.get(0).getType(), "page");
        assertEquals(children.get(1).getType(), "link");
        assertEquals(result.get(0).getTitle(), "Page 1");
    }

    private PageEntity createPage(Long pId, String lang, String title, String titleMenu, Long templateId, PageEntity parent){
        Map<String, PageContentEntity> map = new HashMap<>();
        PageContentEntity pageContentEntity = new PageContentEntity();
        pageContentEntity.setId(pId);
        pageContentEntity.setTitle(title);
        pageContentEntity.setMenuTitle(titleMenu);
        pageContentEntity.setLanguage(lang);
        pageContentEntity.setEnabled(true);
        pageContentEntity.setComputedSlug(CmsStringUtils.toSlug(title));
        map.put(lang, pageContentEntity);

        PageTemplateEntity t = new PageTemplateEntity();
        t.setId(templateId);

        PageEntity p = new PageEntity();
        p.setId(pId);
        p.setContentMap(map);
        p.setPageParent(parent);
        p.setEnabled(true);
        p.setMenuItem(true);
        p.setTemplate(t);
        Mockito.when(pageProvider.findContent(pId)).thenReturn(p);
        return p;
    }
}
