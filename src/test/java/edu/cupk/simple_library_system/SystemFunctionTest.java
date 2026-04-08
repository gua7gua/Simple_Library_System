package edu.cupk.simple_library_system;

import edu.cupk.simple_library_system.common.ApiResponse;
import edu.cupk.simple_library_system.common.GlobalExceptionHandler;
import edu.cupk.simple_library_system.common.PageResponse;
import edu.cupk.simple_library_system.config.WebConfig;
import edu.cupk.simple_library_system.controller.BookInfoController;
import edu.cupk.simple_library_system.controller.BorrowController;
import edu.cupk.simple_library_system.controller.UserController;
import edu.cupk.simple_library_system.repository.BookInfoRepository;
import edu.cupk.simple_library_system.repository.BorrowRepository;
import edu.cupk.simple_library_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class SystemFunctionTest {

    @Autowired
    private WebConfig webConfig;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BookInfoRepository bookInfoRepository;

    @MockitoBean
    private BorrowRepository borrowRepository;

    @Autowired
    private UserController userController;

    @Autowired
    private BookInfoController bookInfoController;

    @Autowired
    private BorrowController borrowController;

    @Test
    void testCorsConfigurationExists() {
        assertNotNull(webConfig);

        CorsRegistry corsRegistry = new CorsRegistry();
        assertDoesNotThrow(() -> webConfig.addCorsMappings(corsRegistry));
    }

    @Test
    void testInterceptorConfiguration() {
        assertNotNull(webConfig);

        InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
        assertDoesNotThrow(() -> webConfig.addInterceptors(interceptorRegistry));
    }

    @Test
    void testResourceHandlerConfiguration() {
        assertNotNull(webConfig);

        ResourceHandlerRegistry resourceHandlerRegistry = mock(ResourceHandlerRegistry.class);
        ResourceHandlerRegistration resourceHandlerRegistration = mock(ResourceHandlerRegistration.class);

        when(resourceHandlerRegistry.addResourceHandler(any(String[].class))).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations(any(String[].class))).thenReturn(resourceHandlerRegistration);

        assertDoesNotThrow(() -> webConfig.addResourceHandlers(resourceHandlerRegistry));

        verify(resourceHandlerRegistry, times(1)).addResourceHandler(any(String[].class));
    }

    @Test
    void testGlobalExceptionHandlerExists() {
        assertNotNull(globalExceptionHandler);
    }

    @Test
    void testHandleException() {
        Exception testException = new Exception("测试异常消息");

        ApiResponse<Void> response = globalExceptionHandler.handleException(testException);

        assertEquals(420, response.getStatus());
        assertEquals("测试异常消息", response.getMessage());
    }

    @Test
    void testHandleExceptionWithNullMessage() {
        Exception testException = new Exception();

        ApiResponse<Void> response = globalExceptionHandler.handleException(testException);

        assertEquals(420, response.getStatus());
        assertEquals("操作失败", response.getMessage());
    }

    @Test
    void testUserCountStatistics() {
        when(userRepository.count()).thenReturn(100L);

        long count = userController.getCount();

        assertEquals(100L, count);
        verify(userRepository, times(1)).count();
    }

    @Test
    void testBookCountStatistics() {
        when(bookInfoRepository.count()).thenReturn(500L);

        long count = bookInfoController.getCount();

        assertEquals(500L, count);
        verify(bookInfoRepository, times(1)).count();
    }

    @Test
    void testBorrowCountStatistics() {
        when(borrowRepository.count()).thenReturn(200L);

        long count = borrowController.getCount();

        assertEquals(200L, count);
        verify(borrowRepository, times(1)).count();
    }

    @Test
    void testComprehensiveStatistics() {
        when(userRepository.count()).thenReturn(50L);
        when(bookInfoRepository.count()).thenReturn(300L);
        when(borrowRepository.count()).thenReturn(150L);

        long userCount = userController.getCount();
        long bookCount = bookInfoController.getCount();
        long borrowCount = borrowController.getCount();

        assertEquals(50L, userCount);
        assertEquals(300L, bookCount);
        assertEquals(150L, borrowCount);
    }

    @Test
    void testApiResponseSuccess() {
        String testData = "测试数据";
        ApiResponse<String> response = ApiResponse.ok("操作成功", testData);

        assertEquals(200, response.getStatus());
        assertEquals("操作成功", response.getMessage());
        assertEquals(testData, response.getData());
        assertTrue(response.getTimestamp() > 0);
    }

    @Test
    void testApiResponseFail() {
        ApiResponse<Void> response = ApiResponse.fail("操作失败");

        assertEquals(420, response.getStatus());
        assertEquals("操作失败", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testPageResponseSuccess() {
        java.util.List<String> dataList = java.util.Arrays.asList("数据1", "数据2", "数据3");
        PageResponse<String> response = PageResponse.success(100L, dataList);

        assertEquals(0, response.getCode());
        assertEquals("success", response.getMessage());
        assertEquals(100L, response.getCount());
        assertEquals(3, response.getData().size());
    }

    @Test
    void testPageResponseEmptyData() {
        PageResponse<String> response = PageResponse.success(0L, java.util.Collections.emptyList());

        assertEquals(0, response.getCode());
        assertEquals(0L, response.getCount());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testApplicationContextLoads() {
        assertTrue(true);
    }

    @Test
    void testWebConfigBeanExists() {
        assertNotNull(webConfig);
    }

    @Test
    void testGlobalExceptionHandlerBeanExists() {
        assertNotNull(globalExceptionHandler);
    }
}
