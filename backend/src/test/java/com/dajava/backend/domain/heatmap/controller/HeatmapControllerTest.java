package com.dajava.backend.domain.heatmap.controller;

// @ActiveProfiles("test")
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// class HeatmapControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@MockitoBean
// 	private HeatmapService heatmapService;
//
// 	private HeatmapResponse mockResponse;
//
// 	@BeforeEach
// 	void setUp() {
// 		// 테스트용 그리드 셀 데이터 생성
// 		List<GridCell> gridCells = new ArrayList<>();
// 		gridCells.add(GridCell.builder()
// 			.gridX(1)
// 			.gridY(2)
// 			.count(10)
// 			.intensity(75)
// 			.build());
//
// 		// 테스트용 메타데이터 생성
// 		HeatmapMetadata metadata = HeatmapMetadata.builder()
// 			.maxCount(10)
// 			.totalEvents(100)
// 			.pageUrl("https://example.com")
// 			.totalSessions(3)
// 			.firstEventTime(LocalDateTime.now().minusHours(1))
// 			.lastEventTime(LocalDateTime.now())
// 			.build();
//
// 		// 테스트용 응답 객체 생성
// 		mockResponse = HeatmapResponse.builder()
// 			.gridSize(10)
// 			.pageWidth(1200)
// 			.pageHeight(3000)
// 			.pageCapture("/captures/test.png")
// 			.gridCells(gridCells)
// 			.metadata(metadata)
// 			.build();
// 	}
//
// 	@Test
// 	@DisplayName("1. 유효한 클릭 타입의 히트맵 조회 테스트")
// 	void t001() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "password123!";
// 		String type = "click";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type)).thenReturn(mockResponse);
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.gridSize").value(10))
// 			.andExpect(jsonPath("$.pageWidth").value(1200))
// 			.andExpect(jsonPath("$.pageHeight").value(3000))
// 			.andExpect(jsonPath("$.pageCapture").value("/captures/test.png"))
// 			.andExpect(jsonPath("$.gridCells[0].gridX").value(1))
// 			.andExpect(jsonPath("$.gridCells[0].gridY").value(2))
// 			.andExpect(jsonPath("$.gridCells[0].count").value(10))
// 			.andExpect(jsonPath("$.gridCells[0].intensity").value(75))
// 			.andExpect(jsonPath("$.metadata.maxCount").value(10))
// 			.andExpect(jsonPath("$.metadata.totalEvents").value(100))
// 			.andExpect(jsonPath("$.metadata.pageUrl").value("https://example.com"))
// 			.andExpect(jsonPath("$.metadata.totalSessions").value(3));
// 	}
//
// 	@Test
// 	@DisplayName("2. 유효한 mousemove 타입의 히트맵 조회 테스트")
// 	void t002() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "password123!";
// 		String type = "mousemove";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type)).thenReturn(mockResponse);
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.gridSize").value(10));
// 	}
//
// 	@Test
// 	@DisplayName("3. 유효한, scroll 타입의 히트맵 조회 테스트")
// 	void t003() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "password123!";
// 		String type = "scroll";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type)).thenReturn(mockResponse);
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.gridSize").value(10));
// 	}
//
// 	@Test
// 	@DisplayName("4. 잘못된 시리얼 번호로 히트맵 조회 시 예외 발생 테스트")
// 	void t004() throws Exception {
// 		// Given
// 		String serialNumber = "INVALID_SN";
// 		String password = "password123!";
// 		String type = "click";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type))
// 			.thenThrow(new HeatmapException(ErrorCode.SOLUTION_SERIAL_NUMBER_INVALID));
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isUnauthorized());
// 	}
//
// 	@Test
// 	@DisplayName("5. 잘못된 비밀번호로 히트맵 조회 시 예외 발생 테스트")
// 	void t005() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "wrong_password";
// 		String type = "click";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type))
// 			.thenThrow(new HeatmapException(ErrorCode.SOLUTION_PASSWORD_INVALID));
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isUnauthorized());
// 	}
//
// 	@Test
// 	@DisplayName("6. 잘못된 이벤트 타입으로 히트맵 조회 시 예외 발생 테스트")
// 	void t006() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "password123!";
// 		String type = "invalid_type";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type))
// 			.thenThrow(new HeatmapException(ErrorCode.INVALID_EVENT_TYPE));
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isBadRequest());
// 	}
//
// 	@Test
// 	@DisplayName("7. 솔루션 데이터가 없을 때 히트맵 조회 시 예외 발생 테스트")
// 	void t007() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "password123!";
// 		String type = "click";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type))
// 			.thenThrow(new HeatmapException(ErrorCode.SOLUTION_DATA_NOT_FOUND));
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isNotFound());
// 	}
//
// 	@Test
// 	@DisplayName("8. 이벤트 데이터가 없을 때 히트맵 조회 시 예외 발생 테스트")
// 	void t008() throws Exception {
// 		// Given
// 		String serialNumber = "5_team_testSerial";
// 		String password = "password123!";
// 		String type = "click";
//
// 		when(heatmapService.getHeatmap(serialNumber, password, type))
// 			.thenThrow(new HeatmapException(ErrorCode.SOLUTION_EVENT_DATA_NOT_FOUND));
//
// 		// When & Then
// 		mockMvc.perform(get("/v1/solution/heatmap/{serialNumber}/{password}", serialNumber, password)
// 				.param("type", type))
// 			.andExpect(status().isNotFound());
// 	}
// }
