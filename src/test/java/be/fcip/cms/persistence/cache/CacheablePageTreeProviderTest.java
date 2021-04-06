package be.fcip.cms.persistence.cache;

import be.fcip.cms.hook.ICacheConfigHook;
import be.fcip.cms.model.MenuItem;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.repository.IPageRepository;
import be.fcip.cms.persistence.service.IWebsiteService;
import be.fcip.cms.util.CmsStringUtils;
import be.fcip.cms.util.CmsUtils;
import org.junit.jupiter.api.BeforeAll;
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

    @InjectMocks
    private CacheablePageTreeProviderImpl cacheablePageTreeProvider;

    public static PageEntity p1, p2, c1, c2, f1, f2, f3;
    public static List<PageEntity> roots = new ArrayList<>();

    @BeforeAll
    public static void setUp(){
        p1 = getPage(1L, "fr", "Page 1", "Menu Page 1", 1L,null);
        c1 = getPage(2L, "fr", "Child 1", "Menu Child 1",  CmsUtils.TEMPLATE_FOLDER_ID, p1);
        c2 = getPage(3L, "fr", "Child 2", "Menu Child 2",  CmsUtils.TEMPLATE_LINK_ID, p1);
        List<PageEntity> children = new ArrayList<>();
        children.add(c1); children.add(c2);
        p1.setPageChildren(children);
        p2 = getPage(4L, "fr", "Page 2", "Menu Page 2", 1L, null);

        f1 = getPage(5L, "fr", "Bad 1", "Menu Bad 1", 1L,null);
        f1.setEnabled(false);
        f2 = getPage(6L, "fr", "Bad 2", "Menu Bad 2", 1L,null);
        f2.setMenuItem(false);
        f3 = getPage(7L, "fr", "Bad 3", "Menu Bad 3", 1L,null);
        PageContentEntity f3Content = f3.getContentMap().get("fr");
        f3Content.setEnabled(false);

        roots.add(p1);roots.add(p2);roots.add(f1);roots.add(f2);roots.add(f3);
    }

    @Test
    void testGetMenuItem(){
        Mockito.when(pageProvider.findContent(1L)).thenReturn(p1);
        Mockito.when(pageProvider.findContent(2L)).thenReturn(c1);
        Mockito.when(pageProvider.findContent(3L)).thenReturn(c2);
        Mockito.when(pageProvider.findContent(4L)).thenReturn(p2);
        Mockito.when(pageProvider.findContent(5L)).thenReturn(f1);
        Mockito.when(pageProvider.findContent(6L)).thenReturn(f2);
        Mockito.when(pageProvider.findContent(7L)).thenReturn(f3);
        Mockito.when(pageRepository.findRootsByPageIdCustom(null, "fr", true, 1L)).thenReturn(roots);

        // Test basic
        List<MenuItem> result = cacheablePageTreeProvider.getMenuItem(null, "fr", 0, 1L, false, null, null, 1L);
        assertNotNull(result);
        assertThat(result, hasSize(2));
        assertNull(result.get(0).getChildren());
        assertTrue(result.get(0).isActive());
        assertEquals(result.get(0).getTitle(), "Menu Page 1");

        // Test depth & onlyTitle & limit
        result = cacheablePageTreeProvider.getMenuItem(null, "fr", 1, null, true, null, 1, 1L);
        assertThat(result, hasSize(1));
        assertFalse(result.get(0).isActive());
        List<MenuItem> children = result.get(0).getChildren();
        assertNotNull(children);
        assertThat(children, hasSize(2));
        assertEquals(children.get(0).getType(), "folder");
        assertEquals(children.get(1).getType(), "link");
        assertEquals(result.get(0).getTitle(), "Page 1");

        // not work Mockito.verify(pageRepository, Mockito.times(1)).findRootsByPageIdCustom(null, "fr", true, 1L);
    }

    private static PageEntity getPage(Long pId, String lang, String title, String titleMenu, Long templateId, PageEntity parent){
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
        return p;
    }

}
