package com.worstcase.outpost.data

/**
 * ============================================================
 *  MOCKDATA.KT
 *  This file contains all the fixed content of the game:
 *  - 4 Earth analog locations connected to real Moon / Mars sites (with 360° views & soundscapes)
 *  - 5 critical crises showcasing comprehensive NASA historical hardware storytelling:
 *      * Surveyor 3 (Moon, 1967) - Catalytic water filters
 *      * Apollo 13 (Moon transit, 1970) - Square-to-round CO2 LiOH scrubbers
 *      * Opportunity Rover (Mars Meridiani Planum, 2004) - Solar array flash heaters & radioisotope units
 *      * Apollo 15 Lunar Roving Vehicle (LRV, 1971) - High-torque DC drive motors & fender dust repairs
 *      * Viking 1 Lander (Mars Chryse Planitia, 1976) - S-band radio dish & thermoelectric generator
 *  - AI Copilot fallback simulation algorithms
 * ============================================================
 */

import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import kotlin.random.Random

object MockData {

    /**
     * Returns the Earth analog locations translated into the player's language.
     * Highlights world-class analogs in Spain (Río Tinto & Lanzarote) alongside global analogs.
     */
    fun getAnalogs(language: AppLanguage): List<Analog> {
        // 12 real NASA / ESA analog training sites worldwide
        return if (language == AppLanguage.SPANISH) {
            listOf(
                Analog(
                    id = "riotinto",
                    name = "Río Tinto (Huelva, España)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Meridiani Planum / Cráter Gale",
                    description = "Análogo geoquímico marciano de la NASA y el CSIC/CAB. Aguas ácidas ricas en hierro y jarosita como en depósitos sulfatados de Marte.",
                    location = "Huelva, España",
                    environmentalModifier = "Acidez extrema y oxidación mineral intensa.",
                    panoramaUrl = "https://mars.nasa.gov/resources/curiosity-360-panorama-gale-crater/",
                    soundscapeDescription = "Vientos áridos y estática de antena."
                ),
                Analog(
                    id = "lanzarote",
                    name = "Geoparque Lanzarote (España)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Marius Hills / Tubos de lava lunares",
                    description = "Campo de entrenamiento ESA PANGAEA. Basaltos y tubos de lava como refugios subterráneos lunares y marcianos.",
                    location = "Islas Canarias, España",
                    environmentalModifier = "Abrasión basáltica y aislamiento subterráneo.",
                    panoramaUrl = "https://www.esa.int/ESA_Multimedia/Images/2017/11/PANGAEA_geological_training_in_Lanzarote",
                    soundscapeDescription = "Eco en tubo volcánico y flujo de traje."
                ),
                Analog(
                    id = "atacama",
                    name = "Desierto de Atacama (Chile)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Desierto marciano hiperárido",
                    description = "Uno de los desiertos más secos de la Tierra. La NASA prueba aquí rovers y detección de vida bajo UV extremo.",
                    location = "Chile",
                    environmentalModifier = "Hiperaridez y radiación UV crítica.",
                    panoramaUrl = "https://mars.nasa.gov/resources/perseverance-360-virtual-tour/",
                    soundscapeDescription = "Ráfagas secas sobre regolito fino."
                ),
                Analog(
                    id = "haughton",
                    name = "Cráter Haughton (Ártico canadiense)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Cráter Shackleton (Polo Sur lunar)",
                    description = "Proyecto Haughton-Mars. Cráter de impacto, permafrost y sombras frías como bases polares lunares.",
                    location = "Isla Devon, Canadá",
                    environmentalModifier = "Frío glacial y terreno de impacto.",
                    panoramaUrl = "https://www.nasa.gov/feature/haughton-mars-project-arctic-analog",
                    soundscapeDescription = "Viento polar y estructuras crujientes."
                ),
                Analog(
                    id = "dryvalleys",
                    name = "Valles Secos de McMurdo (Antártida)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Regiones polares marcianas",
                    description = "Análogo antártico de la NASA: sin nieve, suelos fríos y secos similares a Marte polar.",
                    location = "Antártida",
                    environmentalModifier = "Frío extremo y sequedad polar.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Silencio antártico y viento seco."
                ),
                Analog(
                    id = "hiseas",
                    name = "HI-SEAS (Hawái, EE.UU.)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Hábitat de superficie marciana",
                    description = "Hábitat de simulación NASA en el volcán Mauna Loa para estudios de tripulación aislada de larga duración.",
                    location = "Hawái, EE.UU.",
                    environmentalModifier = "Aislamiento prolongado y terreno volcánico.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Ventilación de hábitat y suelo volcánico."
                ),
                Analog(
                    id = "mdrs",
                    name = "Mars Desert Research Station (Utah)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Operaciones de superficie marciana",
                    description = "Estación del Mars Society usada en estudios de analogía de campo y operaciones EVA estilo Marte.",
                    location = "Utah, EE.UU.",
                    environmentalModifier = "Desierto rojo y polvo abrasivo.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Viento desértico y pasos sobre regolito."
                ),
                Analog(
                    id = "iceland",
                    name = "Campos volcánicos de Islandia",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Terrenos volcánicos marcianos",
                    description = "Basaltos y paisajes volcánicos usados por NASA/ESA para geología planetaria y pruebas de rover.",
                    location = "Islandia",
                    environmentalModifier = "Lava reciente y clima cambiante.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Viento sobre lava y grava volcánica."
                ),
                Analog(
                    id = "svalbard",
                    name = "Svalbard (Ártico noruego)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Exploración polar lunar/marciana",
                    description = "Campañas AMASE de la ESA: geología polar y pruebas de instrumentación en frío extremo.",
                    location = "Noruega",
                    environmentalModifier = "Hielo, frío y logística polar.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Viento ártico y hielo quebrado."
                ),
                Analog(
                    id = "pilbara",
                    name = "Pilbara (Australia)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Astrobiología y rocas antiguas",
                    description = "Terrenos precámbricos usados en estudios de firmas de vida temprana análogas a Marte primitivo.",
                    location = "Australia Occidental",
                    environmentalModifier = "Calor, roca antigua y aislamiento.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Calor seco y viento sobre laterita."
                ),
                Analog(
                    id = "etna",
                    name = "Monte Etna (Italia)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Terreno volcánico y regolito",
                    description = "Campo de pruebas ESA/ASI para robótica y geología en laderas volcánicas activas.",
                    location = "Sicilia, Italia",
                    environmentalModifier = "Ceniza, pendiente y actividad volcánica.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Viento en ladera y grava de lava."
                ),
                Analog(
                    id = "dachstein",
                    name = "Cuevas de Dachstein (Austria)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Hábitats en cueva / tubos de lava",
                    description = "Campamentos análogos CAVES de la ESA: trabajo en equipo en entornos oscuros y confinados.",
                    location = "Alpes austriacos",
                    environmentalModifier = "Oscuridad, humedad y confinamiento.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Goteo de cueva y eco lejano."
                )
                ,
                Analog(
                    id = "concordia",
                    name = "Base Concordia (Antártida)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Aislamiento polar / noche permanente",
                    description = "Estación franco-italiana ESA: aislamiento extremo, psicología de tripulación y operaciones en frío profundo.",
                    location = "Domo C, Antártida",
                    environmentalModifier = "Frío extremo, aislamiento y noche polar.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Viento polar y generadores lejanos."
                ),
                Analog(
                    id = "blackpoint",
                    name = "Black Point Lava Flow (Arizona, EE.UU.)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Campos de lava y regolito",
                    description = "Campo de pruebas NASA para trajes, rovers y operaciones de superficie en terreno volcánico.",
                    location = "Arizona, EE.UU.",
                    environmentalModifier = "Lava abrasiva y terreno irregular.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Viento seco sobre basalto."
                ),
                Analog(
                    id = "moses_lake",
                    name = "Moses Lake (Washington, EE.UU.)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Pruebas de aterrizaje / superficie",
                    description = "Usado por NASA para ensayos de sistemas de aterrizaje y movilidad en terreno abierto.",
                    location = "Washington, EE.UU.",
                    environmentalModifier = "Polvo, espacio abierto y viento.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Viento de llanura."
                ),
                Analog(
                    id = "flagstaff",
                    name = "Flagstaff / campo volcánico (Arizona)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Entrenamiento geológico Apolo",
                    description = "Histórico entrenamiento geológico de tripulaciones Apolo en terrenos volcánicos del norte de Arizona.",
                    location = "Arizona, EE.UU.",
                    environmentalModifier = "Altitude, basalto y cráteres de impacto de ensayo.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Bosque de altura y viento."
                ),
                Analog(
                    id = "boulby",
                    name = "Mina de Boulby (Reino Unido)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Laboratorio subterráneo / astrobiología",
                    description = "Laboratorio profundo usado en ciencia análoga y detección de vida; analogía de entornos protegidos bajo superficie.",
                    location = "Yorkshire, Reino Unido",
                    environmentalModifier = "Profundidad, oscuridad y confinamiento.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Eco de galería y ventilación."
                ),
                Analog(
                    id = "tabernas",
                    name = "Desierto de Tabernas (España)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Desierto árido / operaciones de campo",
                    description = "Desierto continental europeo usado en ensayos de robótica y geología de superficie.",
                    location = "Almería, España",
                    environmentalModifier = "Aridez, polvo y alta insolación.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Viento de badlands."
                ),
                Analog(
                    id = "namib",
                    name = "Desierto del Namib (Namibia)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Dunas y hiperaridez",
                    description = "Uno de los desiertos más antiguos; análogo de dunas, polvo y preservación de humedad.",
                    location = "Namibia",
                    environmentalModifier = "Dunas, hiperaridez y viento abrasivo.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Arena en movimiento."
                ),
                Analog(
                    id = "meteor_crater",
                    name = "Meteor Crater (Arizona, EE.UU.)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Cráter de impacto",
                    description = "Cráter de impacto bien preservado; referencia clásica de geología de impactos para entrenamiento.",
                    location = "Arizona, EE.UU.",
                    environmentalModifier = "Borde de cráter y eyecta.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Viento en el borde del cráter."
                ),
                Analog(
                    id = "lunares",
                    name = "LUNARES (Polonia)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Hábitat análogo / aislamiento",
                    description = "Hábitat análogo europeo para misiones simuladas de superficie y factores humanos.",
                    location = "Polonia",
                    environmentalModifier = "Confinamiento de hábitat y protocolos EVA simulados.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Ventilación de módulo."
                ),
                Analog(
                    id = "nemo",
                    name = "NEEMO (Florida, EE.UU.)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Operaciones bajo estrés / EVA análoga",
                    description = "Hábitat submarino NASA: analogía de aislamiento, retraso y trabajo EVA en entorno hostil.",
                    location = "Florida Keys, EE.UU.",
                    environmentalModifier = "Presión, humedad y aislamiento submarino.",
                    panoramaUrl = "https://www.nasa.gov/mission_pages/NEEMO/",
                    soundscapeDescription = "Burbujas y sistemas de soporte vital."
                ),
                Analog(
                    id = "wadi_rum",
                    name = "Wadi Rum (Jordania)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Desierto rojo / operaciones de campo",
                    description = "Terreno desértico usado en ensayos y divulgación de exploración tipo Marte.",
                    location = "Jordania",
                    environmentalModifier = "Arenisca, polvo y calor diurno.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Eco de cañón y viento."
                ),
                Analog(
                    id = "cinder_lake",
                    name = "Cinder Lake (Arizona, EE.UU.)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Campo de cráteres de ensayo Apolo",
                    description = "Campo de cráteres artificiales usado en la era Apolo para practicar navegación y muestreo.",
                    location = "Cerca de Flagstaff, Arizona",
                    environmentalModifier = "Ceniza volcánica y cráteres de práctica.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Grava volcánica bajo las botas."
                )

            )
        } else {
            listOf(
                Analog(
                    id = "riotinto",
                    name = "Río Tinto (Huelva, Spain)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Meridiani Planum / Gale Crater",
                    description = "NASA and CAB/CSIC geochemical Mars analog. Acidic, iron-rich waters mirror sulfate deposits found by Mars rovers.",
                    location = "Huelva, Spain",
                    environmentalModifier = "Extreme acidity and mineral oxidation.",
                    panoramaUrl = "https://mars.nasa.gov/resources/curiosity-360-panorama-gale-crater/",
                    soundscapeDescription = "Arid low-pressure gusts and antenna static."
                ),
                Analog(
                    id = "lanzarote",
                    name = "Lanzarote Geopark (Spain)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Marius Hills / Lunar lava tubes",
                    description = "ESA PANGAEA astronaut field site. Volcanic basalts and lava tubes as lunar/Martian subsurface habitat analogs.",
                    location = "Canary Islands, Spain",
                    environmentalModifier = "Basaltic abrasion and underground isolation.",
                    panoramaUrl = "https://www.esa.int/ESA_Multimedia/Images/2017/11/PANGAEA_geological_training_in_Lanzarote",
                    soundscapeDescription = "Cave echo and suit airflow."
                ),
                Analog(
                    id = "atacama",
                    name = "Atacama Desert (Chile)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Hyper-arid Martian desert",
                    description = "Among Earth's driest places. NASA tests rovers and life-detection under extreme UV.",
                    location = "Chile",
                    environmentalModifier = "Hyper-aridity and critical UV.",
                    panoramaUrl = "https://mars.nasa.gov/resources/perseverance-360-virtual-tour/",
                    soundscapeDescription = "Dry gusts over fine regolith."
                ),
                Analog(
                    id = "haughton",
                    name = "Haughton Crater (Canadian Arctic)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Shackleton Crater (lunar south pole)",
                    description = "Haughton-Mars Project impact crater, permafrost and cold shadows for polar base training.",
                    location = "Devon Island, Canada",
                    environmentalModifier = "Glacial cold and impact terrain.",
                    panoramaUrl = "https://www.nasa.gov/feature/haughton-mars-project-arctic-analog",
                    soundscapeDescription = "Polar wind and structure creaks."
                ),
                Analog(
                    id = "dryvalleys",
                    name = "McMurdo Dry Valleys (Antarctica)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Martian polar regions",
                    description = "NASA Antarctic analog: cold, dry, nearly snow-free soils similar to polar Mars.",
                    location = "Antarctica",
                    environmentalModifier = "Extreme cold and polar dryness.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Antarctic silence and dry wind."
                ),
                Analog(
                    id = "hiseas",
                    name = "HI-SEAS (Hawaii, USA)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Mars surface habitat",
                    description = "NASA isolation habitat on Mauna Loa for long-duration crew behavioral studies.",
                    location = "Hawaii, USA",
                    environmentalModifier = "Long isolation and volcanic terrain.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Habitat fans and volcanic soil."
                ),
                Analog(
                    id = "mdrs",
                    name = "Mars Desert Research Station (Utah)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Mars surface operations",
                    description = "Mars Society station used for field analog research and EVA-style operations.",
                    location = "Utah, USA",
                    environmentalModifier = "Red desert dust and abrasion.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Desert wind and regolith steps."
                ),
                Analog(
                    id = "iceland",
                    name = "Icelandic volcanic fields",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Martian volcanic terrains",
                    description = "Basalt landscapes used by NASA/ESA for planetary geology and rover trials.",
                    location = "Iceland",
                    environmentalModifier = "Young lava and shifting weather.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Wind over lava gravel."
                ),
                Analog(
                    id = "svalbard",
                    name = "Svalbard (Norwegian Arctic)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Polar exploration",
                    description = "ESA AMASE campaigns: polar geology and instrument tests in extreme cold.",
                    location = "Norway",
                    environmentalModifier = "Ice, cold and polar logistics.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Arctic wind and cracking ice."
                ),
                Analog(
                    id = "pilbara",
                    name = "Pilbara (Australia)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Early Mars astrobiology",
                    description = "Ancient Precambrian terrains used for early-life signature studies relevant to Mars.",
                    location = "Western Australia",
                    environmentalModifier = "Heat, ancient rock and remoteness.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Hot dry wind on laterite."
                ),
                Analog(
                    id = "etna",
                    name = "Mount Etna (Italy)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Volcanic regolith slopes",
                    description = "ESA/ASI robotics and geology field tests on active volcanic slopes.",
                    location = "Sicily, Italy",
                    environmentalModifier = "Ash, slope and volcanic activity.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Slope wind and lava gravel."
                ),
                Analog(
                    id = "dachstein",
                    name = "Dachstein caves (Austria)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Cave / lava-tube habitats",
                    description = "ESA CAVES analog camps: teamwork in dark, confined underground environments.",
                    location = "Austrian Alps",
                    environmentalModifier = "Darkness, humidity and confinement.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Cave drip and distant echo."
                )
                ,
                Analog(
                    id = "concordia",
                    name = "Concordia Station (Antarctica)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Polar isolation / permanent night",
                    description = "French–Italian ESA station: extreme isolation, crew psychology and deep-cold operations.",
                    location = "Dome C, Antarctica",
                    environmentalModifier = "Extreme cold, isolation and polar night.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Polar wind and distant generators."
                ),
                Analog(
                    id = "blackpoint",
                    name = "Black Point Lava Flow (Arizona, USA)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Lava fields and regolith",
                    description = "NASA field site for suits, rovers and surface ops on volcanic terrain.",
                    location = "Arizona, USA",
                    environmentalModifier = "Abrasive lava and rough terrain.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Dry wind over basalt."
                ),
                Analog(
                    id = "moses_lake",
                    name = "Moses Lake (Washington, USA)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Landing / surface test range",
                    description = "Used by NASA for landing-system and mobility trials on open terrain.",
                    location = "Washington, USA",
                    environmentalModifier = "Dust, open space and wind.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Plain wind."
                ),
                Analog(
                    id = "flagstaff",
                    name = "Flagstaff volcanic field (Arizona)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Apollo geology training",
                    description = "Historic Apollo geology training on northern Arizona volcanic terrain.",
                    location = "Arizona, USA",
                    environmentalModifier = "Altitude, basalt and training craters.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "High forest wind."
                ),
                Analog(
                    id = "boulby",
                    name = "Boulby Mine (United Kingdom)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Underground lab / astrobiology",
                    description = "Deep laboratory used for analog science and life detection; protected subsurface environments.",
                    location = "Yorkshire, UK",
                    environmentalModifier = "Depth, darkness and confinement.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Gallery echo and ventilation."
                ),
                Analog(
                    id = "tabernas",
                    name = "Tabernas Desert (Spain)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Arid field operations",
                    description = "Europe’s continental desert used for robotics and surface geology trials.",
                    location = "Almería, Spain",
                    environmentalModifier = "Aridity, dust and high insolation.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Badlands wind."
                ),
                Analog(
                    id = "namib",
                    name = "Namib Desert (Namibia)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Dunes and hyper-aridity",
                    description = "One of Earth’s oldest deserts; dune, dust and moisture-preservation analog.",
                    location = "Namibia",
                    environmentalModifier = "Dunes, hyper-aridity and abrasive wind.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Moving sand."
                ),
                Analog(
                    id = "meteor_crater",
                    name = "Meteor Crater (Arizona, USA)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Impact crater",
                    description = "Well-preserved impact crater; classic impact-geology reference for training.",
                    location = "Arizona, USA",
                    environmentalModifier = "Crater rim and ejecta.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Wind on the rim."
                ),
                Analog(
                    id = "lunares",
                    name = "LUNARES habitat (Poland)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Analog habitat / isolation",
                    description = "European analog habitat for simulated surface missions and human factors.",
                    location = "Poland",
                    environmentalModifier = "Habitat confinement and simulated EVA protocols.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Module ventilation."
                ),
                Analog(
                    id = "nemo",
                    name = "NEEMO (Florida, USA)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Ops under stress / EVA analog",
                    description = "NASA underwater habitat: isolation, delay and EVA-like work in a hostile environment.",
                    location = "Florida Keys, USA",
                    environmentalModifier = "Pressure, humidity and underwater isolation.",
                    panoramaUrl = "https://www.nasa.gov/mission_pages/NEEMO/",
                    soundscapeDescription = "Bubbles and life-support systems."
                ),
                Analog(
                    id = "wadi_rum",
                    name = "Wadi Rum (Jordan)",
                    planetaryTarget = PlanetaryTarget.MARS,
                    targetSite = "Red desert / field ops",
                    description = "Desert terrain used for Mars-like exploration trials and outreach.",
                    location = "Jordan",
                    environmentalModifier = "Sandstone, dust and daytime heat.",
                    panoramaUrl = "https://www.esa.int/",
                    soundscapeDescription = "Canyon echo and wind."
                ),
                Analog(
                    id = "cinder_lake",
                    name = "Cinder Lake (Arizona, USA)",
                    planetaryTarget = PlanetaryTarget.MOON,
                    targetSite = "Apollo practice crater field",
                    description = "Artificial crater field used in the Apollo era for navigation and sampling practice.",
                    location = "Near Flagstaff, Arizona",
                    environmentalModifier = "Volcanic cinders and practice craters.",
                    panoramaUrl = "https://www.nasa.gov/",
                    soundscapeDescription = "Cinder gravel under boots."
                )

            )
        }
    }

    /** Backward-compatible default list of analogs */
    val analogs: List<Analog> get() = getAnalogs(AppLanguage.ENGLISH)

    /**
     * Returns the expanded 5 critical events localized according to the player's language.
     * Incorporates rich NASA hardware history and Junior astronaut training hints.
     */
    fun getCrises(language: AppLanguage): List<Crisis> =
        getCrises(language, destination = null)

    /**
     * Crisis catalog filtered by planetary target when provided.
     * Shared crises appear for both Moon and Mars; body-specific ones do not.
     */
    fun getCrises(language: AppLanguage, destination: PlanetaryTarget?): List<Crisis> {
        val all = getCrisesAll(language)
        val moonOnly = setOf("micrometeorite_breach", "telemetry_blackout", "suit_leak", "nav_glitch")
        val marsOnly = setOf("solar_storm", "dust_storm_filter", "crop_failure", "battery_thermal")
        val eligible = all.filter { c ->
            when (destination) {
                PlanetaryTarget.MOON -> c.id !in marsOnly
                PlanetaryTarget.MARS -> c.id !in moonOnly
                null -> true
            }
        }.ifEmpty { all }
        val spanish = language == AppLanguage.SPANISH
        return eligible.map { crisis ->
            crisis.copy(options = crisis.options.map { option ->
                if (!option.usesAbandonedHardware) option else option.copy(
                    text = option.text + (if (spanish) " — réplica de entrenamiento" else " — training replica"),
                    hardwareHistory = (if (spanish)
                        "Ejercicio en un análogo terrestre con réplicas del inventario preparado. No se recupera equipo real de otro planeta. Caso histórico:\n\n"
                    else "Earth-analog exercise using replicas from the prepared inventory. No real equipment is recovered from another planet. Historical case:\n\n") +
                        option.hardwareHistory.orEmpty()
                )
            })
        }
    }

    private fun getCrisesAll(language: AppLanguage): List<Crisis> {
        return if (language == AppLanguage.SPANISH) {
            listOf(
                // Crisis 1 – Reciclador de agua (Surveyor 3)
                Crisis(
                    id = "water_recycling",
                    title = "Fallo Crítico en el Reciclador de Agua",
                    description = "Los condensadores electroquímicos del circuito primario de bucle cerrado han colapsado. La reserva potable caerá drásticamente si no se actúa ya.",
                    juniorHint = "💡 Consejo de Vuelo: El agua es el recurso más pesado de transportar desde la Tierra. Racionarla salva depósitos pero destruye la moral y fatiga a los astronautas.",
                    options = listOf(
                        CrisisOption(
                            id = "repair",
                            text = "Reparar el reciclador: recuperar agua con repuestos y trabajo de mantenimiento",
                            waterDelta = 16,
                            energyDelta = -14,
                            moraleDelta = -3,
                            sleepDelta = -10, stressDelta = 4
                        ),
                        CrisisOption(
                            id = "ration",
                            text = "Racionamiento estricto de consumo hídrico",
                            waterDelta = -8,
                            moraleDelta = -18,
                            healthDelta = -10, sleepDelta = -12, stressDelta = 8
                        ),
                        CrisisOption(
                            id = "abandoned_surveyor",
                            text = "Canibalizar filtros y celdas de la etapa de descenso Surveyor 3",
                            waterDelta = 24,
                            energyDelta = -18,
                            moraleDelta = 8,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🛰️ Surveyor 3 (Oceanus Procellarum, 1967). La sonda aterrizó en la Luna como parte del programa que preparó Apolo. En noviembre de 1969, Conrad y Bean (Apolo 12) alunizaron a ~180 m, recuperaron la cámara de TV y tubos de aluminio y los llevaron a la Tierra. En laboratorio se estudió la degradación por vacío, radiación y polvo: las aleaciones y sellos mantuvieron integridad estructural. Fue una de las primeras pruebas reales de que el hardware abandonado en la Luna podía recuperarse y analizarse años después.",
                            sleepDelta = -12, stressDelta = 6
                        )
                    )
                ),

                // Crisis 2 – Tormenta solar / radiación (Opportunity)
                Crisis(
                    id = "solar_storm",
                    title = "Tormenta Solar – Eyección de Masa Coronal",
                    description = "Los sensores orbitales detectan una protonada solar inminente. El campo de radiación alcanzará niveles letales para la tripulación expuesta.",
                    juniorHint = "💡 Consejo de Vuelo: En emergencias solares, la energía eléctrica es secundaria; proteger los tejidos biológicos y la visión de la tripulación en el búnker es la máxima prioridad.",
                    options = listOf(
                        CrisisOption(
                            id = "shelter",
                            text = "Confinar a la tripulación en el refugio blindado de regolito",
                            energyDelta = -10,
                            healthDelta = 0,
                            moraleDelta = 4,
                            sleepDelta = 2, stressDelta = -4, scienceDelta = -2
                        ),
                        CrisisOption(
                            id = "power_down",
                            text = "Mantener tareas esenciales: menor coste energético, exposición limitada",
                            energyDelta = -6,
                            healthDelta = -3, moraleDelta = -2,
                            sleepDelta = -6, stressDelta = 4
                        ),
                        CrisisOption(
                            id = "abandoned_opportunity",
                            text = "Recuperar RHU de Opportunity: ahorrar calefacción, con mayor exposición",
                            energyDelta = 20,
                            healthDelta = -12,
                            moraleDelta = 6,
                            sleepDelta = -12, stressDelta = 8,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🤖 Opportunity (Marte, 2004–2018). Diseñado para ~90 soles, operó casi 15 años y recorrió más de 45 km. Sus calentadores RHU de radioisótopos protegieron la electrónica en noches gélidas. Murió tras la tormenta global de polvo de 2018, cuando los paneles dejaron de cargar. Demostró que el hardware de superficie puede superar con creces su vida nominal si se gestiona energía, polvo y termalización — lección clave para reutilizar o extender sistemas en Marte."
                        )
                    )
                ),

                // Crisis 3 – Depurador CO2 (Apolo 13)
                Crisis(
                    id = "system_failure",
                    title = "Fallo en el Depurador de Dióxido de Carbono",
                    description = "El depurador de CO₂ del módulo de vida se ha obstruido. Los niveles de CO₂ en el aire respirable aumentan con rapidez.",
                    juniorHint = "💡 Consejo de Vuelo: Un aumento en CO2 induce confusión mental y asfixia en minutos. Si una solución no convencional funciona, los astronautas ganan esperanza.",
                    options = listOf(
                        CrisisOption(
                            id = "standard_repair",
                            text = "Reparar el depurador con repuestos: recuperar atmósfera respirable",
                            oxygenDelta = 12,
                            energyDelta = -10,
                            healthDelta = 0,
                            sleepDelta = -10, stressDelta = 5
                        ),
                        CrisisOption(
                            id = "cannibalize_apollo13",
                            text = "Adaptar filtros de hidróxido de litio como en Apolo 13",
                            oxygenDelta = 20,
                            energyDelta = -16,
                            moraleDelta = 12,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🚀 Apolo 13 (abril 1970). La explosión del tanque de O₂ en el módulo de servicio obligó a apagar sistemas y usar el módulo lunar como bote salvavidas. El CO₂ subía: los filtros del módulo de mando (CM) eran cuadrados y las aberturas del sistema del módulo lunar (LM), redondas. En Houston idearon un adaptador con cinta americana, bolsas y portadas del manual de vuelo para utilizar los filtros del CM en el LM. Lovell, Swigert y Haise regresaron vivos. Es el ejemplo canónico de reutilizar y adaptar lo que hay a bordo bajo presión extrema.",
                            sleepDelta = -12, stressDelta = 6
                        ),
                        CrisisOption(
                            id = "evacuate_module",
                            text = "Aislar y despresurizar el módulo de habitabilidad",
                            oxygenDelta = -12,
                            moraleDelta = -25,
                            healthDelta = -8
                        )
                    )
                ),

                // Crisis 4 – Rotura de sello presurizado por impacto de micrometeorito (Apolo 15 LRV)
                Crisis(
                    id = "micrometeorite_breach",
                    title = "Impacto de Micrometeorito y Fuga de Presión",
                    description = "Un impacto hiperveloz ha perforado el conducto exterior de interconexión. El regolito levantado por la descompresión desgasta las juntas.",
                    juniorHint = "💡 Consejo de Vuelo: El polvo lunar y marciano es abrasivo como cristal picado. Utilizar guardabarros y fibras duras improvisadas frena la fuga sin agotar masillas.",
                    options = listOf(
                        CrisisOption(
                            id = "seal_paste",
                            text = "Inyectar resina selladora epoxi espacial (reserva limitada)",
                            oxygenDelta = 6,
                            energyDelta = -8,
                            waterDelta = -3,
                            stressDelta = 4
                        ),
                        CrisisOption(
                            id = "abandoned_lrv",
                            text = "Reparar parche con guardabarros del Rover Lunar Apolo 15",
                            oxygenDelta = 12,
                            energyDelta = -14,
                            moraleDelta = 10,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🛞 Rover lunar LRV (Apolo 15–17, 1971–72). En Apolo 17, Cernan rompió un guardabarros; el polvo lunar abrasivo ensuciaba trajes y cámaras. Improvisaron un guardabarros con mapas laminados y cinta. Los LRV siguen en la Luna (Hadley, Descartes, Taurus-Littrow). Muestran que el polvo es un riesgo de misión y que el inventiva con materiales a bordo puede salvar movilidad y ciencia.",
                            sleepDelta = -12, stressDelta = 6
                        ),
                        CrisisOption(
                            id = "depressurize_airlock",
                            text = "Cerrar compuertas de emergencia y sacrificar esclusa",
                            oxygenDelta = -18,
                            moraleDelta = -15,
                            healthDelta = -5
                        )
                    )
                ),

                // Crisis 5 – Pérdida de enlace de telemetría de espacio profundo (Viking 1)
                Crisis(
                    id = "telemetry_blackout",
                    title = "Apagón de Comunicaciones de Espacio Profundo",
                    description = "El transpondedor primario de banda X ha sufrido una sobretensión estática. La base está incomunicada con el Control de Misión en la Tierra.",
                    juniorHint = "💡 Consejo de Vuelo: Sin enlace de telemetría con la Tierra, los astronautas quedan aislados psicológicamente. Un enlace secundario de baja ganancia restablece la seguridad.",
                    options = listOf(
                        CrisisOption(
                            id = "manual_align",
                            text = "Calibrar la antena de reserva desde el hábitat: gastar energía y restaurar enlace",
                            energyDelta = -6,
                            healthDelta = 0,
                            moraleDelta = 8,
                            stressDelta = 4
                        ),
                        CrisisOption(
                            id = "abandoned_viking",
                            text = "Adaptar antena Viking: recuperar energía y enlace con mayor exposición",
                            energyDelta = 8,
                            moraleDelta = 15,
                            usesAbandonedHardware = true,
                            hardwareHistory = "📡 Viking 1 (Chryse Planitia, 1976). Primer aterrizaje exitoso prolongado en Marte (~6 años de datos). Combinó experimentos biológicos, imágenes y meteorología con RTG SNAP-19 y radio en banda S. Fijó estándares de energía nuclear superficial y enlace directo Tierra–Marte. Su longevidad inspira la idea de legar estaciones o componentes útiles para misiones posteriores.",
                            healthDelta = -4, sleepDelta = -14, stressDelta = 8
                        ),
                        CrisisOption(
                            id = "wait_orbit",
                            text = "Aplazar la reparación para conservar energía: consumir raciones y moral",
                            moraleDelta = -22,
                            foodDelta = -2,
                            sleepDelta = 4, stressDelta = 8
                        )
                    )
                )

                ,
                Crisis(
                    id = "dust_storm_filter",
                    title = "Filtros de polvo saturados",
                    description = "Una tormenta local satura los filtros del hábitat. El flujo de aire cae y sube el riesgo de CO₂ y partículas en cabina.",
                    juniorHint = "💡 Prioriza ventilación y recambio de filtros antes de que suba el CO₂.",
                    options = listOf(
                        CrisisOption(id = "swap_filters", text = "Sustituir filtros con stock de misión", energyDelta = -8, oxygenDelta = 5, moraleDelta = -3),
                        CrisisOption(id = "reduce_activity", text = "Reducir actividad: conservar energía y descanso, con menor calidad del aire", oxygenDelta = -5, moraleDelta = -10, sleepDelta = 4, stressDelta = -2, scienceDelta = -1),
                        CrisisOption(id = "heritage_filter", text = "Adaptar malla histórica: más recuperación de aire, mayor energía y trabajo", energyDelta = -12, oxygenDelta = 10, moraleDelta = 8, sleepDelta = -12, stressDelta = 6, usesAbandonedHardware = true,
                            hardwareHistory = "En Marte, el polvo es un riesgo recurrente (p. ej. Opportunity 2018). Gestionar filtros y energía es lección operativa real.")
                    )
                ),
                Crisis(
                    id = "battery_thermal",
                    title = "Excursión térmica en banco de baterías",
                    description = "Un string de baterías se calienta fuera de rango. Hay riesgo de desconexión o degradación permanente.",
                    juniorHint = "💡 Equilibra carga, disipación térmica y carga crítica del hábitat.",
                    options = listOf(
                        CrisisOption(id = "shed_loads", text = "Aliviar cargas: poco trabajo y menor gasto, pero baja la moral", energyDelta = -4, moraleDelta = -5, sleepDelta = -4, stressDelta = 1),
                        CrisisOption(id = "active_cool", text = "Refrigerar para recuperar energía: gastar agua y asumir exposición", energyDelta = 6, waterDelta = -4, healthDelta = -3, sleepDelta = -10, stressDelta = 6),
                        CrisisOption(id = "isolate_string", text = "Aislar string y redistribuir", energyDelta = -6, moraleDelta = 4)
                    )
                ),
                Crisis(
                    id = "suit_leak",
                    title = "Microfuga en traje EVA",
                    description = "Durante preparación EVA se detecta una caída lenta de presión en un traje. La ventana de actividad se estrecha.",
                    juniorHint = "💡 No ignores una microfuga: sella, prueba y solo entonces sal.",
                    options = listOf(
                        CrisisOption(id = "seal_kit", text = "Kit de sellado y retest de presión", energyDelta = -4, healthDelta = -2, moraleDelta = 5),
                        CrisisOption(id = "abort_eva", text = "Abortar EVA: proteger salud y descanso, aplazando ciencia", moraleDelta = -12, sleepDelta = 6, stressDelta = -4, scienceDelta = -2),
                        CrisisOption(id = "swap_suit", text = "Usar y comprobar un traje de reserva: más energía y trabajo, mayor seguridad", energyDelta = -10, moraleDelta = 8, healthDelta = 2, sleepDelta = -10, stressDelta = 4)
                    )
                ),
                Crisis(
                    id = "crop_failure",
                    title = "Estrés en módulo de cultivo",
                    description = "La iluminación LED falla a medias y parte del cultivo se pierde. Impacto en comida y moral.",
                    juniorHint = "💡 La agricultura espacial es frágil: prioriza potencia estable y humedad.",
                    options = listOf(
                        CrisisOption(id = "reroute_power", text = "Reencaminar potencia a luces de cultivo", energyDelta = -14, foodDelta = 1, moraleDelta = 6),
                        CrisisOption(id = "accept_loss", text = "Aceptar pérdida y racionar", foodDelta = -2, moraleDelta = -14),
                        CrisisOption(id = "spare_leds", text = "Reparar LEDs: menor gasto energético, sin ganar días de raciones", energyDelta = -8, foodDelta = 0, moraleDelta = 4)
                    )
                ),
                Crisis(
                    id = "nav_glitch",
                    title = "Glitch en navegación de superficie",
                    description = "El odometer/IMU del rover de base deriva. El regreso al outpost puede alargarse.",
                    juniorHint = "💡 Usa referencias visuales y reduce velocidad antes de confiar en la IMU sola.",
                    options = listOf(
                        CrisisOption(id = "dead_reckon", text = "Navegación a estima con hitos", energyDelta = -6, healthDelta = -4, moraleDelta = -4),
                        CrisisOption(id = "radio_beacon", text = "Guiado por baliza del outpost", energyDelta = -10, moraleDelta = 6),
                        CrisisOption(id = "stop_wait", text = "Detenerse y esperar corrección de tierra", moraleDelta = -8, foodDelta = -1, sleepDelta = 4)
                    )
                ),
                Crisis(
                    id = "medical_fatigue",
                    title = "Fatiga acumulada de tripulación",
                    description = "Tras jornadas intensas, sueño y estrés cruzan umbrales. Sube el riesgo de error humano.",
                    juniorHint = "💡 El factor humano es un sistema de misión: programa descanso aunque retrase tareas.",
                    options = listOf(
                        CrisisOption(id = "sleep_shift", text = "Turno de sueño obligatorio: recuperar descanso y aplazar ciencia", moraleDelta = 12, foodDelta = -1, energyDelta = -4, sleepDelta = 20, stressDelta = -15, healthDelta = 4, scienceDelta = -2),
                        CrisisOption(id = "stim_push", text = "Mantener actividad: ganar energía a costa de sueño, estrés y salud", healthDelta = -8, moraleDelta = -6, energyDelta = 4, sleepDelta = -16, stressDelta = 12),
                        CrisisOption(id = "share_load", text = "Redistribuir tareas: recuperación moderada sin aplazar ciencia", moraleDelta = 8, energyDelta = -6, sleepDelta = 8, stressDelta = -8, healthDelta = 2)
                    )
                ),
                Crisis(
                    id = "fire_alarm",
                    title = "Alarma de humo en rack aviónico",
                    description = "Sensor de humo en un rack. Puede ser polvo… o un principio de incendio en microgravedad/superficie.",
                    juniorHint = "💡 Trata toda alarma como real hasta demostrar lo contrario: aislar, ventilar, verificar.",
                    options = listOf(
                        CrisisOption(id = "power_isolate", text = "Aislar rack y usar extintor", energyDelta = -16, oxygenDelta = -6, moraleDelta = -5),
                        CrisisOption(id = "inspect_only", text = "Inspección rápida sin cortar potencia", healthDelta = -12, energyDelta = -4, moraleDelta = -10),
                        CrisisOption(id = "vent_module", text = "Ventilar módulo y reiniciar", oxygenDelta = -10, energyDelta = -8, moraleDelta = 2)
                    )
                )

            )
        } else {
            listOf(
                // Crisis 1 – Water recycling failure (Surveyor 3)
                Crisis(
                    id = "water_recycling",
                    title = "Critical Failure in Water Recovery System",
                    description = "Electrochemical catalytic condensers in the primary water recovery loop have suffered thermal breakdown. Available water reserves are plunging.",
                    juniorHint = "💡 Flight Director Advice: Water is the heaviest cargo to launch from Earth. Rationing saves mass but degrades astronaut stamina and induces severe psychological friction.",
                    options = listOf(
                        CrisisOption(
                            id = "repair",
                            text = "Repair the recycler: recover water using spares and maintenance work",
                            waterDelta = 16,
                            energyDelta = -14,
                            moraleDelta = -3,
                            sleepDelta = -10, stressDelta = 4
                        ),
                        CrisisOption(
                            id = "ration",
                            text = "Impose draconian water consumption rationing",
                            waterDelta = -8,
                            moraleDelta = -18,
                            healthDelta = -10, sleepDelta = -12, stressDelta = 8
                        ),
                        CrisisOption(
                            id = "abandoned_surveyor",
                            text = "Salvage catalytic filters from abandoned Surveyor 3 lander",
                            waterDelta = 24,
                            energyDelta = -18,
                            moraleDelta = 8,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🛰️ Surveyor 3 (Ocean of Storms, 1967). Soft-landed to scout Apollo sites. In Nov 1969, Conrad and Bean (Apollo 12) set down ~180 m away, removed the TV camera and aluminum tubes, and returned them to Earth. Labs studied vacuum, radiation and dust effects: alloys and seals kept structural integrity. Early proof that hardware left on the Moon can be recovered and reused as engineering knowledge — and, in future concepts, as physical assets.",
                            sleepDelta = -12, stressDelta = 6
                        )
                    )
                ),

                // Crisis 2 – Solar storm / radiation event (Opportunity)
                Crisis(
                    id = "solar_storm",
                    title = "Solar Storm – Coronal Mass Ejection",
                    description = "Orbital coronagraphs confirm an energetic proton storm is heading directly toward the outpost. Unprotected EVA and habitat radiation will spike dangerously.",
                    juniorHint = "💡 Flight Director Advice: In radiation emergencies, equipment power is secondary. Protecting human biological cells in the storm shelter is paramount.",
                    options = listOf(
                        CrisisOption(
                            id = "shelter",
                            text = "Order immediate full lockdown in the regolith bunker",
                            energyDelta = -10,
                            healthDelta = 0,
                            moraleDelta = 4,
                            sleepDelta = 2, stressDelta = -4, scienceDelta = -2
                        ),
                        CrisisOption(
                            id = "power_down",
                            text = "Keep essential tasks running: lower energy cost, limited exposure",
                            energyDelta = -6,
                            healthDelta = -3, moraleDelta = -2,
                            sleepDelta = -6, stressDelta = 4
                        ),
                        CrisisOption(
                            id = "abandoned_opportunity",
                            text = "Salvage Opportunity RHUs: save heating energy, with greater exposure",
                            energyDelta = 20,
                            healthDelta = -12,
                            moraleDelta = 6,
                            sleepDelta = -12, stressDelta = 8,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🤖 Opportunity (Mars, 2004–2018). Built for ~90 sols, it worked nearly 15 years and drove 45+ km. RHU heaters protected electronics through freezing nights. A global dust storm in 2018 coated the arrays and ended the mission. Lesson: surface systems can far outlive design life if power, dust and thermal margins are managed — core thinking for extending or scavenging Martian hardware."
                        )
                    )
                ),

                // Crisis 3 – Life support failure (Apollo 13)
                Crisis(
                    id = "system_failure",
                    title = "Critical Carbon Dioxide Scrubber Jam",
                    description = "Lithium hydroxide canisters in the primary air revitalizer have clogged. Atmospheric CO2 levels are steadily climbing toward narcotic toxicity.",
                    juniorHint = "💡 Flight Director Advice: High CO2 causes cognitive lethargy and hypoxia. Repurposing historical ingenuity restores morale and oxygen reserves.",
                    options = listOf(
                        CrisisOption(
                            id = "standard_repair",
                            text = "Repair the scrubber with spares: recover breathable atmosphere",
                            oxygenDelta = 12,
                            energyDelta = -10,
                            healthDelta = 0,
                            sleepDelta = -10, stressDelta = 5
                        ),
                        CrisisOption(
                            id = "cannibalize_apollo13",
                            text = "Adapt historical Apollo 13 LiOH canister adaptation protocol",
                            oxygenDelta = 20,
                            energyDelta = -16,
                            moraleDelta = 12,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🚀 Apollo 13 (April 1970). An SM oxygen-tank explosion forced power-downs and use of the lunar module as a lifeboat. CO₂ rose: command module (CM) canisters were square, while the lunar module (LM) system had round openings. Houston devised an adapter with duct tape, plastic bags and flight-manual covers so the CM canisters could be used in the LM. Lovell, Swigert and Haise returned alive. Canonical example of reusing and adapting what is already on board under extreme pressure.",
                            sleepDelta = -12, stressDelta = 6
                        ),
                        CrisisOption(
                            id = "evacuate_module",
                            text = "Seal and abandon primary living module",
                            oxygenDelta = -12,
                            moraleDelta = -25,
                            healthDelta = -8
                        )
                    )
                ),

                // Crisis 4 – Micrometeorite hull breach (Apollo 15 LRV)
                Crisis(
                    id = "micrometeorite_breach",
                    title = "Micrometeorite Hull Breach and Depressurization",
                    description = "A hypervelocity fragment has pierced an external umbilical raceway. Decompression vortex is blowing abrasive lunar/martian dust across junction seals.",
                    juniorHint = "💡 Flight Director Advice: Regolith shards act like airborne diamond grit. Flexible high-density guards deflect abrasion and buy time for seal curing.",
                    options = listOf(
                        CrisisOption(
                            id = "seal_paste",
                            text = "Inject quick-curing epoxy space sealant (scarce reserves)",
                            oxygenDelta = 6,
                            energyDelta = -8,
                            waterDelta = -3,
                            stressDelta = 4
                        ),
                        CrisisOption(
                            id = "abandoned_lrv",
                            text = "Bolt improvised patch using Apollo 15 Lunar Roving Vehicle fender",
                            oxygenDelta = 12,
                            energyDelta = -14,
                            moraleDelta = 10,
                            usesAbandonedHardware = true,
                            hardwareHistory = "🛞 Apollo Lunar Roving Vehicle (LRV, Apollo 15–17, 1971–72). On Apollo 17 Cernan damaged a fender; abrasive lunar dust fouled suits and cameras. They jury-rigged a fender from laminated maps and tape. LRVs still sit at Hadley, Descartes and Taurus-Littrow. Lesson: dust is a mission hazard, and onboard improvisation can save mobility and science.",
                            sleepDelta = -12, stressDelta = 6
                        ),
                        CrisisOption(
                            id = "depressurize_airlock",
                            text = "Slam airlock bulkheads and abandon external vestibule",
                            oxygenDelta = -18,
                            moraleDelta = -15,
                            healthDelta = -5
                        )
                    )
                ),

                // Crisis 5 – Telemetry Blackout (Viking 1)
                Crisis(
                    id = "telemetry_blackout",
                    title = "Deep Space Network Telemetry Blackout",
                    description = "High-voltage static discharge has fried the primary high-gain X-band transponder. The base is completely deaf and blind to NASA Mission Control.",
                    juniorHint = "💡 Flight Director Advice: Loss of ground link triggers acute isolation anxiety. Restoring an S-band downlink restores mission safety margins.",
                    options = listOf(
                        CrisisOption(
                            id = "manual_align",
                            text = "Calibrate the backup antenna from the habitat: spend energy and restore contact",
                            energyDelta = -6,
                            healthDelta = 0,
                            moraleDelta = 8,
                            stressDelta = 4
                        ),
                        CrisisOption(
                            id = "abandoned_viking",
                            text = "Adapt a Viking antenna: recover energy and contact with greater exposure",
                            energyDelta = 8,
                            moraleDelta = 15,
                            usesAbandonedHardware = true,
                            hardwareHistory = "📡 Viking 1 (Chryse Planitia, 1976). First long-lived successful Mars lander (~6 years of data). Combined biology experiments, imaging and weather with SNAP-19 RTGs and S-band radio. Set patterns for surface nuclear power and direct Earth–Mars links. Longevity inspires leaving stations or parts useful to later crews.",
                            healthDelta = -4, sleepDelta = -14, stressDelta = 8
                        ),
                        CrisisOption(
                            id = "wait_orbit",
                            text = "Postpone repairs to conserve energy: spend rations and morale",
                            moraleDelta = -22,
                            foodDelta = -2,
                            sleepDelta = 4, stressDelta = 8
                        )
                    )
                )

                ,
                Crisis(
                    id = "dust_storm_filter",
                    title = "Dust filters saturated",
                    description = "A local dust event clogs habitat filters. Airflow drops and CO₂ / particle risk rises.",
                    juniorHint = "💡 Prioritize ventilation and filter swaps before CO₂ climbs.",
                    options = listOf(
                        CrisisOption(id = "swap_filters", text = "Replace filters from mission stock", energyDelta = -8, oxygenDelta = 5, moraleDelta = -3),
                        CrisisOption(id = "reduce_activity", text = "Reduce activity: conserve energy and rest, with lower air quality", oxygenDelta = -5, moraleDelta = -10, sleepDelta = 4, stressDelta = -2, scienceDelta = -1),
                        CrisisOption(id = "heritage_filter", text = "Adapt heritage mesh: more air recovery, more energy and work", energyDelta = -12, oxygenDelta = 10, moraleDelta = 8, sleepDelta = -12, stressDelta = 6, usesAbandonedHardware = true,
                            hardwareHistory = "Martian dust is a recurring operational hazard (e.g. Opportunity, 2018). Filter and power management remain core lessons.")
                    )
                ),
                Crisis(
                    id = "battery_thermal",
                    title = "Battery bank thermal excursion",
                    description = "A battery string runs hot. Risk of disconnect or permanent degradation.",
                    juniorHint = "💡 Balance load shed, cooling and critical habitat power.",
                    options = listOf(
                        CrisisOption(id = "shed_loads", text = "Shed loads: less work and lower cost, but morale drops", energyDelta = -4, moraleDelta = -5, sleepDelta = -4, stressDelta = 1),
                        CrisisOption(id = "active_cool", text = "Cool to recover energy: spend water and accept exposure", energyDelta = 6, waterDelta = -4, healthDelta = -3, sleepDelta = -10, stressDelta = 6),
                        CrisisOption(id = "isolate_string", text = "Isolate string and redistribute", energyDelta = -6, moraleDelta = 4)
                    )
                ),
                Crisis(
                    id = "suit_leak",
                    title = "EVA suit micro-leak",
                    description = "During EVA prep, suit pressure drifts down slowly. The activity window shrinks.",
                    juniorHint = "💡 Never ignore a micro-leak: seal, retest, only then go.",
                    options = listOf(
                        CrisisOption(id = "seal_kit", text = "Seal kit and pressure retest", energyDelta = -4, healthDelta = -2, moraleDelta = 5),
                        CrisisOption(id = "abort_eva", text = "Abort EVA: protect health and rest, postpone science", moraleDelta = -12, sleepDelta = 6, stressDelta = -4, scienceDelta = -2),
                        CrisisOption(id = "swap_suit", text = "Switch and check a spare suit: more energy and work, better safety", energyDelta = -10, moraleDelta = 8, healthDelta = 2, sleepDelta = -10, stressDelta = 4)
                    )
                ),
                Crisis(
                    id = "crop_failure",
                    title = "Crop module stress",
                    description = "LED lighting fails partially and part of the crop is lost. Food and morale take a hit.",
                    juniorHint = "💡 Space agriculture is fragile: stable power and humidity first.",
                    options = listOf(
                        CrisisOption(id = "reroute_power", text = "Reroute power to grow lights", energyDelta = -14, foodDelta = 1, moraleDelta = 6),
                        CrisisOption(id = "accept_loss", text = "Accept loss and ration food", foodDelta = -2, moraleDelta = -14),
                        CrisisOption(id = "spare_leds", text = "Repair LEDs: lower energy cost, no extra ration-days", energyDelta = -8, foodDelta = 0, moraleDelta = 4)
                    )
                ),
                Crisis(
                    id = "nav_glitch",
                    title = "Surface navigation glitch",
                    description = "Base rover IMU/odometry drifts. Return to the outpost may take longer.",
                    juniorHint = "💡 Use visual landmarks and slow down before trusting IMU alone.",
                    options = listOf(
                        CrisisOption(id = "dead_reckon", text = "Dead-reckon with landmarks", energyDelta = -6, healthDelta = -4, moraleDelta = -4),
                        CrisisOption(id = "radio_beacon", text = "Home on outpost radio beacon", energyDelta = -10, moraleDelta = 6),
                        CrisisOption(id = "stop_wait", text = "Stop and wait for ground update", moraleDelta = -8, foodDelta = -1, sleepDelta = 4)
                    )
                ),
                Crisis(
                    id = "medical_fatigue",
                    title = "Accumulated crew fatigue",
                    description = "After intense days, sleep and stress cross thresholds. Human-error risk rises.",
                    juniorHint = "💡 Human factors are a mission system: schedule rest even if tasks slip.",
                    options = listOf(
                        CrisisOption(id = "sleep_shift", text = "Mandatory sleep shift: recover rest and postpone science", moraleDelta = 12, foodDelta = -1, energyDelta = -4, sleepDelta = 20, stressDelta = -15, healthDelta = 4, scienceDelta = -2),
                        CrisisOption(id = "stim_push", text = "Keep working: gain energy at the cost of rest, stress and health", healthDelta = -8, moraleDelta = -6, energyDelta = 4, sleepDelta = -16, stressDelta = 12),
                        CrisisOption(id = "share_load", text = "Redistribute tasks: moderate recovery without postponing science", moraleDelta = 8, energyDelta = -6, sleepDelta = 8, stressDelta = -8, healthDelta = 2)
                    )
                ),
                Crisis(
                    id = "fire_alarm",
                    title = "Smoke alarm in avionics rack",
                    description = "Smoke sensor trips on a rack. It may be dust — or a real fire start.",
                    juniorHint = "💡 Treat every alarm as real until proven otherwise: isolate, ventilate, verify.",
                    options = listOf(
                        CrisisOption(id = "power_isolate", text = "Isolate rack and use extinguisher", energyDelta = -16, oxygenDelta = -6, moraleDelta = -5),
                        CrisisOption(id = "inspect_only", text = "Quick inspect without cutting power", healthDelta = -12, energyDelta = -4, moraleDelta = -10),
                        CrisisOption(id = "vent_module", text = "Vent module and restart", oxygenDelta = -10, energyDelta = -8, moraleDelta = 2)
                    )
                )

            )
        }
    }

    /** Backward-compatible default list of crises */
    val crises: List<Crisis> get() = getCrises(AppLanguage.ENGLISH)

    /**
     * Evaluates tactical flight director assessment simulating an on-device AI Copilot.
     * Analyzes current outpost state, active crisis, and hardware heritage options.
     */

    /**
     * Long-form 360 briefing: technical, historical and ops notes per analog.
     * ES / EN for now; other UI languages receive English scientific text.
     */
    fun analogDeepBriefing(id: String, language: AppLanguage): Triple<String, String, String> {
        val es = language == AppLanguage.SPANISH
        fun t(en: String, esText: String) = if (es) esText else en
        return when (id) {
            "riotinto" -> Triple(
                t("Acid drainage and iron oxides mirror sulfate chemistry studied at Meridiani and Gale. Sensors must tolerate low pH and mineral precipitation on optics.",
                  "Drenaje ácido y óxidos de hierro reflejan la química de sulfatos de Meridiani y Gale. Los sensores deben tolerar pH bajo y precipitación mineral en ópticas."),
                t("Río Tinto has been used for decades as a Mars geochemical analog by CAB/CSIC and NASA collaborators for extremophile and mineralogy work.",
                  "Río Tinto se usa desde hace décadas como análogo geoquímico de Marte por CAB/CSIC y colaboradores NASA en extremófilos y mineralogía."),
                t("Ops: limit open-helmet time, rinse sample tools, log pH with every EVA simulation.",
                  "Ops: limitar tiempo con casco abierto, enjuagar herramientas de muestreo y registrar pH en cada EVA simulada.")
            )
            "lanzarote" -> Triple(
                t("Basaltic tubes and lava fields support EVA navigation, suit abrasion tests and cave-habitat logistics (PANGAEA / ESA field campaigns).",
                  "Tubos basálticos y campos de lava sirven para navegación EVA, abrasión de trajes y logística de hábitats en cueva (PANGAEA / campañas ESA)."),
                t("Lanzarote Geopark hosts ESA astronaut geology training (PANGAEA) linking volcanic terrain to lunar highlands operations.",
                  "El Geoparque de Lanzarote aloja entrenamiento geológico ESA (PANGAEA) que enlaza terreno volcánico con operaciones en tierras altas lunares."),
                t("Ops: mark tube entrances, manage dust on seals, rehearse one-fault lighting failures underground.",
                  "Ops: marcar bocas de tubo, controlar polvo en sellos y ensayar fallos de iluminación bajo tierra.")
            )
            "atacama" -> Triple(
                t("Hyper-arid soils and intense UV stress power systems, dust mitigation and life-detection false positives — core Mars surface lessons.",
                  "Suelos hiperaridos y UV intenso estresan potencia, mitigación de polvo y falsos positivos de detección de vida: lecciones clave de superficie marciana."),
                t("NASA and international teams use Atacama for rover trials and astrobiology because liquid water is nearly absent at the surface.",
                  "NASA y equipos internacionales usan Atacama para rovers y astrobiología porque el agua líquida es casi ausente en superficie."),
                t("Ops: schedule high-load work at low Sun angle; bag optics; track electrostatic dust on connectors.",
                  "Ops: programar cargas altas con sol bajo; proteger ópticas; vigilar polvo electrostático en conectores.")
            )
            "haughton" -> Triple(
                t("Polar impact crater with continuous permafrost: thermal cycling, EVA in cold and remote logistics like lunar high-latitude sites.",
                  "Cráter de impacto polar con permafrost: ciclado térmico, EVA en frío y logística remota como sitios lunares de alta latitud."),
                t("Haughton-Mars Project and related Arctic campaigns trained instruments and crews for Moon/Mars-relevant cold ops.",
                  "El proyecto Haughton-Mars y campañas árticas entrenaron instrumentos y tripulaciones en operaciones en frío tipo Luna/Marte."),
                t("Ops: anti-fog protocols, battery warm storage, strict lost-person search radii on featureless plains.",
                  "Ops: protocolos anti-vaho, baterías en caliente y radios de búsqueda estrictos en llanuras sin referencias.")
            )
            "dryvalleys" -> Triple(
                t("Antarctic Dry Valleys combine cold desert hydrology and microbial refugia — analogs for icy Moon/Mars subsurface niches.",
                  "Los Valles Secos antárticos combinan hidrología de desierto frío y refugios microbianos: análogos de nichos bajo superficie en Luna/Marte."),
                t("Long-term NSF/NASA science here informed planetary protection and cold-desert instrument design.",
                  "La ciencia NSF/NASA a largo plazo aquí informó protección planetaria y diseño de instrumentos de desierto frío."),
                t("Ops: minimize contamination, manage melt-water season windows, protect cables from freeze-thaw.",
                  "Ops: minimizar contaminación, gestionar ventanas de deshielo y proteger cables del hielo-deshielo.")
            )
            "hiseas" -> Triple(
                t("HI-SEAS habitat isolation studies fatigue, crew dynamics and delayed communication on a volcanic Mars-like slope.",
                  "HI-SEAS estudia aislamiento, fatiga, dinámica de tripulación y comunicación retardada en ladera volcánica tipo Marte."),
                t("University of Hawaiʻi / NASA-funded missions simulated months-long surface stays with limited resupply.",
                  "Misiones de la University of Hawaiʻi / NASA simularon estancias de meses con reabastecimiento limitado."),
                t("Ops: protect sleep shifts, script family-comm windows, drill habitat leak responses weekly.",
                  "Ops: proteger turnos de sueño, ventanas de coms familiares y ensayar fugas de hábitat semanalmente.")
            )
            "mdrs" -> Triple(
                t("MDRS Utah desert habitat drills rover traverses, EVA timelines and science sampling under arid constraints.",
                  "MDRS en el desierto de Utah ensaya travesías de rover, tiempos EVA y muestreo científico bajo aridez."),
                t("The Mars Society station has hosted hundreds of crews practicing surface mission workflows.",
                  "La estación de The Mars Society ha alojado cientos de tripulaciones practicando flujos de misión de superficie."),
                t("Ops: enforce suit-don timers, dust lock procedures, and sample chain-of-custody logs.",
                  "Ops: tiempos de vestido, esclusas de polvo y registro de custodia de muestras.")
            )
            "iceland" -> Triple(
                t("Basaltic terrains and glaciers support volcanic and ice-interaction training for Moon/Mars geology EVAs.",
                  "Terrenos basálticos y glaciares permiten entrenamiento volcánico y de interacción con hielo para EVA geológicas Luna/Marte."),
                t("ESA and NASA field campaigns use Iceland for planetary geology and rover mobility on rough lava.",
                  "Campañas ESA y NASA usan Islandia para geología planetaria y movilidad de rovers en lava irregular."),
                t("Ops: watch crevasse/ice hazards, map unstable lava crusts, limit solo traverses.",
                  "Ops: vigilar grietas/hielo, mapear costras de lava inestables y limitar travesías en solitario.")
            )
            "svalbard" -> Triple(
                t("High Arctic logistics, polar night psychology and cold-instrument ops map to long lunar nights.",
                  "Logística ártica, psicología de noche polar y operaciones en frío se mapean a largas noches lunares."),
                t("AMASE and related campaigns tested life-detection and field robotics in Svalbard.",
                  "AMASE y campañas relacionadas probaron detección de vida y robótica de campo en Svalbard."),
                t("Ops: redundant heating, strict buddy rules, staged depot caches along routes.",
                  "Ops: calefacción redundante, regla de compañero y depósitos escalonados en rutas.")
            )
            "pilbara" -> Triple(
                t("Ancient Archean outcrops train recognition of early-Earth biosignatures relevant to Mars sample science.",
                  "Afloramientos arcaicos entrenan el reconocimiento de biofirmas de la Tierra primitiva relevantes para ciencia de muestras en Marte."),
                t("Western Australia Pilbara is a classic field classroom for NASA/ESA-linked geobiology training.",
                  "Pilbara (Australia Occidental) es un aula de campo clásica para entrenamiento de geobiología ligado a NASA/ESA."),
                t("Ops: protect stromatolite sites, sterile sampling kits, detailed photo-documentation before contact.",
                  "Ops: proteger estromatolitos, kits estériles y fotodocumentación detallada antes del contacto.")
            )
            "etna" -> Triple(
                t("Active volcanic slopes stress mobility, ash contamination and rapid weather changes for surface ops.",
                  "Laderas volcánicas activas estresan movilidad, contaminación por ceniza y cambios rápidos de tiempo."),
                t("ESA/ASI and partners use Etna for robotics and geology under real volcanic ash risk.",
                  "ESA/ASI y socios usan el Etna para robótica y geología con riesgo real de ceniza."),
                t("Ops: filter changes after ash falls, abort criteria for seismicity, eye/respirator discipline.",
                  "Ops: cambio de filtros tras ceniza, criterios de aborto por sismicidad y protección respiratoria.")
            )
            "dachstein" -> Triple(
                t("Cave networks train confined navigation, communication dead-zones and team roles under stress (ESA CAVES).",
                  "Redes de cuevas entrenan navegación confinada, zonas sin radio y roles de equipo bajo estrés (ESA CAVES)."),
                t("ESA CAVES uses Dachstein and similar systems to prepare astronauts for partial gravity exploration teamwork.",
                  "ESA CAVES usa Dachstein y sistemas similares para preparar trabajo en equipo de exploración."),
                t("Ops: line surveys, three-point contact, mandatory turn-around times on consumables.",
                  "Ops: topografía de línea, contacto a tres puntos y tiempos máximos por consumibles.")
            )
            "concordia" -> Triple(
                t("Dome C isolation, altitude-like physiology and months of darkness stress behavioural health and systems reliability.",
                  "Aislamiento en Domo C, fisiología de altitud y meses de oscuridad estresan salud conductual y fiabilidad de sistemas."),
                t("Concordia (IPEV/PNRA, ESA research) is a premier Antarctic station for human factors relevant to deep-space transit.",
                  "Concordia (IPEV/PNRA, investigación ESA) es referencia antártica de factores humanos para tránsito de espacio profundo."),
                t("Ops: enforced circadian lighting, medical telemetry schedules, winter-over conflict mediation drills.",
                  "Ops: iluminación circadiana, telemetría médica y mediación de conflictos en invernada.")
            )
            "blackpoint" -> Triple(
                t("Lava fields for suit, rover and ISRU field trials with abrasive basaltic substrate.",
                  "Campos de lava para trajes, rovers e ISRU sobre sustrato basáltico abrasivo."),
                t("NASA Desert RATS and related Arizona campaigns used Black Point–class terrains for surface architecture tests.",
                  "Desert RATS y campañas en Arizona usaron terrenos tipo Black Point para arquitecturas de superficie."),
                t("Ops: track tread wear, seal inspections after each EVA, staged recovery vehicles.",
                  "Ops: desgaste de orugas, inspección de sellos tras cada EVA y vehículos de recuperación.")
            )
            "moses_lake" -> Triple(
                t("Open ranges for landing-system and large-vehicle mobility under dust and wind.",
                  "Campos abiertos para sistemas de aterrizaje y movilidad de vehículos grandes con polvo y viento."),
                t("NASA has used Moses Lake areas for flight and surface test support infrastructure.",
                  "NASA ha usado zonas de Moses Lake como apoyo a ensayos de vuelo y superficie."),
                t("Ops: FOD walks, wind limits for crane ops, clear abort corridors.",
                  "Ops: barridos FOD, límites de viento para grúas y corredores de aborto claros.")
            )
            "flagstaff" -> Triple(
                t("Volcanic field and cratered terrain used since Apollo for crew geology walks and navigation drills.",
                  "Campo volcánico y cráteres usados desde Apolo para geología de tripulación y navegación."),
                t("Apollo crews trained near Flagstaff; USGS and NASA heritage remains central to lunar geology teaching.",
                  "Las tripulaciones Apolo entrenaron cerca de Flagstaff; el legado USGS/NASA sigue en la enseñanza de geología lunar."),
                t("Ops: traverse maps with time tags, sample priority lists, contingency rally points.",
                  "Ops: mapas con tiempos, listas de prioridad de muestras y puntos de reunión.")
            )
            "boulby" -> Triple(
                t("Deep mine laboratory: low-background physics and subsurface analog access for instrument calibration.",
                  "Laboratorio en mina profunda: física de bajo fondo y acceso subsuperficial para calibrar instrumentos."),
                t("Boulby hosts astrobiology and dark-matter related work useful for planetary instrument noise floors.",
                  "Boulby aloja astrobiología y trabajo relacionado con materia oscura útil para ruido de instrumentos planetarios."),
                t("Ops: gas monitors, refuge chambers, strict elevator/man-riding schedules.",
                  "Ops: monitores de gas, cámaras de refugio y horarios estrictos de jaula.")
            )
            "tabernas" -> Triple(
                t("European continental desert for robotics, dust and thermal-soak tests without leaving the EU.",
                  "Desierto continental europeo para robótica, polvo y ensayos térmicos sin salir de la UE."),
                t("Almería Tabernas supports ESA and industry field trials in arid badlands.",
                  "Tabernas (Almería) apoya ensayos de campo ESA e industria en badlands áridos."),
                t("Ops: midday thermal limits, dust covers on mechanisms, water cache discipline.",
                  "Ops: límites térmicos al mediodía, cubiertas anti-polvo y disciplina de depósitos de agua.")
            )
            "namib" -> Triple(
                t("Hyper-arid dunes: mobility in soft sand, moisture-preservation studies and dust electrification.",
                  "Dunas hiperaridas: movilidad en arena blanda, preservación de humedad y electrificación del polvo."),
                t("Namib is among Earth’s oldest deserts; used in planetary analog literature for dune processes.",
                  "El Namib es de los desiertos más antiguos; aparece en literatura análoga por procesos de dunas."),
                t("Ops: recovery boards for rovers, electrostatic grounding, never solo dune crests in high wind.",
                  "Ops: tablas de recuperación, toma de tierra electrostática y nunca crestas en solitario con viento.")
            )
            "meteor_crater" -> Triple(
                t("Simple bowl crater morphology for impact-geology teaching and rim-traverse safety.",
                  "Morfología de cráter simple para geología de impactos y seguridad en el borde."),
                t("Barringer Meteor Crater is a classic Apollo-era and modern training reference for impact structures.",
                  "El Meteor Crater de Barringer es referencia clásica Apolo y moderna de estructuras de impacto."),
                t("Ops: stay off unstable rim segments, document ejecta blankets before sampling.",
                  "Ops: evitar bordes inestables y documentar eyecta antes de muestrear.")
            )
            "lunares" -> Triple(
                t("Habitat isolation analog: airlock cycles, timeline pressure and small-team coordination.",
                  "Análogo de hábitat: ciclos de esclusa, presión de timeline y coordinación de equipos pequeños."),
                t("LUNARES (Poland) runs simulated surface missions focused on human factors and procedures.",
                  "LUNARES (Polonia) ejecuta misiones de superficie simuladas centradas en factores humanos y procedimientos."),
                t("Ops: written handover logs, silent-failure drills, capped private-comm minutes.",
                  "Ops: relevos escritos, fallos silenciosos ensayados y minutos de coms privadas limitados.")
            )
            "nemo" -> Triple(
                t("Underwater habitat: delayed help, suit-like constraints and team medical readiness (NASA NEEMO).",
                  "Hábitat submarino: ayuda retardada, restricciones tipo traje y preparación médica (NASA NEEMO)."),
                t("NEEMO missions place astronauts and engineers in Aquarius to rehearse exploration under stress.",
                  "NEEMO sitúa astronautas e ingenieros en Aquarius para ensayar exploración bajo estrés."),
                t("Ops: no-untended buddy rule, medical kit inventories each watch, abort-to-surface criteria.",
                  "Ops: regla de compañero, inventario médico por guardia y criterios de aborto a superficie.")
            )
            "wadi_rum" -> Triple(
                t("Red-sandstone desert for long visual traverses, dust and thermal cycling similar to Mars outreach/field films and tests.",
                  "Desierto de arenisca roja para travesías largas, polvo y ciclado térmico tipo Marte."),
                t("Wadi Rum is frequently used for Mars-like field imagery and analog expedition logistics in the region.",
                  "Wadi Rum se usa a menudo para imagen de campo tipo Marte y logística de expediciones análogas."),
                t("Ops: heat-illness watch, vehicle recovery points, night nav with dust-reduced lamps.",
                  "Ops: vigilancia de golpe de calor, puntos de recuperación y navegación nocturna con luces anti-polvo.")
            )
            "cinder_lake" -> Triple(
                t("Artificial crater field built for Apollo crew practice of navigation and sampling among many rims.",
                  "Campo de cráteres artificiales del programa Apolo para practicar navegación y muestreo entre bordes."),
                t("Cinder Lake near Flagstaff was intentionally blasted to mimic lunar crater density for crew walks.",
                  "Cinder Lake (Flagstaff) se excavó para imitar densidad de cráteres lunares en caminatas de tripulación."),
                t("Ops: time-tagged waypoints, no shortcut across soft cinder, photo before each sample bag.",
                  "Ops: waypoints con tiempo, no atajar por ceniza blanda y foto antes de cada bolsa de muestra.")
            )
            else -> Triple(
                t("Technical notes will expand as more field data is linked to this site.",
                  "Las notas técnicas se ampliarán cuando haya más datos de campo enlazados a este sitio."),
                t("Historical analog use varies by agency campaign (NASA, ESA and partners).",
                  "El uso histórico como análogo varía según campañas NASA, ESA y socios."),
                t("Ops: apply standard EVA timeline, dust and consumable discipline.",
                  "Ops: aplicar timeline EVA estándar y disciplina de polvo y consumibles.")
            )
        }
    }

    /** Extra life-support tax from the selected Earth analog environment (per crisis). */
    fun analogRiskTax(analogId: String?): Triple<Int, Int, Int> {
        // water, oxygen, energy extra burn
        return when (analogId) {
            "atacama", "namib", "tabernas", "mdrs" -> Triple(1, 1, 1) // dust / aridity
            "haughton", "svalbard", "concordia", "dryvalleys" -> Triple(0, 1, 2) // cold power
            "riotinto" -> Triple(2, 0, 1) // corrosion / water chemistry
            "etna", "blackpoint", "lanzarote" -> Triple(0, 1, 1) // ash / abrasion
            "nemo", "dachstein", "boulby", "lunares" -> Triple(0, 1, 0) // confined CO2 risk proxy
            else -> Triple(0, 0, 0)
        }
    }


    /**
     * Localize crisis title (and short junior hint header) for the 10 UI languages.
     * Full narrative body stays ES/EN from the catalog; titles always match UI language.
     */
    fun localizeCrisis(crisis: Crisis, language: AppLanguage): Crisis {
        val title = crisisTitle(crisis.id, language) ?: crisis.title
        return crisis.copy(title = title)
    }

    private fun crisisTitle(id: String, language: AppLanguage): String? {
        // map: id -> (en, es, fr, pt, de, it, ja, ko, uk, ar)
        val table: Map<String, List<String>> = mapOf(
            "water_recycling" to listOf(
                "Critical failure in water recovery",
                "Fallo crítico en el reciclador de agua",
                "Panne critique du recycleur d'eau",
                "Falha crítica no reciclador de água",
                "Kritischer Ausfall der Wasseraufbereitung",
                "Guasto critico del riciclatore d'acqua",
                "水再生システムの重大故障",
                "물 재생 시스템 중대 고장",
                "Критична відмова рециклера води",
                "عطل حرج في إعادة تدوير المياه"
            ),
            "solar_storm" to listOf(
                "Solar storm / radiation event",
                "Tormenta solar / evento de radiación",
                "Tempête solaire / événement de radiation",
                "Tempestade solar / evento de radiação",
                "Sonnensturm / Strahlungsereignis",
                "Tempesta solare / evento di radiazione",
                "太陽嵐／放射線イベント",
                "태양 폭풍 / 방사선 사건",
                "Сонячна буря / радіаційна подія",
                "عاصفة شمسية / حدث إشعاعي"
            ),
            "system_failure" to listOf(
                "Critical system failure",
                "Fallo crítico de sistema",
                "Panne système critique",
                "Falha crítica de sistema",
                "Kritischer Systemausfall",
                "Guasto critico di sistema",
                "システム重大故障",
                "시스템 중대 고장",
                "Критична відмова системи",
                "عطل نظام حرج"
            ),
            "micrometeorite_breach" to listOf(
                "Micrometeorite breach",
                "Brecha por micrometeorito",
                "Brèche par micrométéorite",
                "Brecha por micrometeorito",
                "Mikrometeoriten-Einschlag",
                "Breccia da micrometeorite",
                "微隕石による貫通",
                "미세 운석 관통",
                "Пробоїна від мікрометеорита",
                "اختراق نيزك دقيق"
            ),
            "telemetry_blackout" to listOf(
                "Deep-space communications blackout",
                "Apagón de comunicaciones de espacio profundo",
                "Black-out des communications spatiales",
                "Apagão de comunicações de espaço profundo",
                "Ausfall der Tiefenraum-Kommunikation",
                "Blackout delle comunicazioni profonde",
                "深宇宙通信の途絶",
                "심우주 통신 두절",
                "Відмова зв'язку далекого космосу",
                "انقطاع اتصالات الفضاء العميق"
            ),
            "dust_storm_filter" to listOf(
                "Dust filters saturated",
                "Filtros de polvo saturados",
                "Filtres à poussière saturés",
                "Filtros de pó saturados",
                "Staubfilter gesättigt",
                "Filtri antipolvere saturi",
                "ダストフィルタ飽和",
                "분진 필터 포화",
                "Засмічені пилові фільтри",
                "تشبع مرشحات الغبار"
            ),
            "battery_thermal" to listOf(
                "Battery thermal excursion",
                "Excursión térmica de baterías",
                "Excursion thermique des batteries",
                "Excursão térmica das baterias",
                "Thermische Batterie-Abweichung",
                "Escursione termica batterie",
                "バッテリー熱逸走",
                "배터리 열 이상",
                "Теплова аномалія батарей",
                "انحراف حراري للبطاريات"
            ),
            "suit_leak" to listOf(
                "EVA suit micro-leak",
                "Microfuga en traje EVA",
                "Microfuite de combinaison EVA",
                "Microfuga no traje EVA",
                "Mikro-Leck im EVA-Anzug",
                "Microfuga tuta EVA",
                "EVAスーツ微少漏洩",
                "EVA 슈트 미세 누출",
                "Мікровитік скафандра EVA",
                "تسرب دقيق في بدلة EVA"
            ),
            "crop_failure" to listOf(
                "Crop module stress",
                "Estrés en módulo de cultivo",
                "Stress du module de culture",
                "Estresse no módulo de cultivo",
                "Stress im Anbaumodul",
                "Stress del modulo di coltivazione",
                "栽培モジュールの異常",
                "재배 모듈 스트레스",
                "Стрес модуля вирощування",
                "إجهاد وحدة الزراعة"
            ),
            "nav_glitch" to listOf(
                "Surface navigation glitch",
                "Glitch de navegación de superficie",
                "Glitch de navigation de surface",
                "Falha de navegação de superfície",
                "Fehler in der Oberflächennavigation",
                "Glitch di navigazione di superficie",
                "表面航法の不具合",
                "표면 항법 오류",
                "Збій навігації на поверхні",
                "خلل في ملاحة السطح"
            ),
            "medical_fatigue" to listOf(
                "Accumulated crew fatigue",
                "Fatiga acumulada de tripulación",
                "Fatigue cumulée d'équipage",
                "Fadiga acumulada da tripulação",
                "Kumulierte Crew-Ermüdung",
                "Fatica accumulata dell'equipaggio",
                "乗組員の蓄積疲労",
                "승무원 누적 피로",
                "Накопичена втома екіпажу",
                "إرهاق متراكم للطاقم"
            ),
            "fire_alarm" to listOf(
                "Smoke alarm in avionics rack",
                "Alarma de humo en rack aviónico",
                "Alarme fumée baie avionique",
                "Alarme de fumaça no rack aviônico",
                "Rauchalarm im Avionik-Rack",
                "Allarme fumo rack avionica",
                "アビオニクス架の煙警報",
                "항공전자 랙 연기 경보",
                "Димова сигналізація авіоніки",
                "إنذار دخان في رف إلكترونيات الطيران"
            )
        )
        val row = table[id] ?: return null
        val index = when (language) {
            AppLanguage.ENGLISH -> 0
            AppLanguage.SPANISH -> 1
            AppLanguage.FRENCH -> 2
            AppLanguage.PORTUGUESE -> 3
            AppLanguage.GERMAN -> 4
            AppLanguage.ITALIAN -> 5
            AppLanguage.JAPANESE -> 6
            AppLanguage.KOREAN -> 7
            AppLanguage.UKRAINIAN -> 8
            AppLanguage.ARABIC -> 9
            else -> 0
        }
        return row.getOrNull(index) ?: row[0]
    }

    fun evaluateAiCopilot(
        crisis: Crisis, resources: Resources, language: AppLanguage,
        outcomes: Map<String, DecisionOutcome> = emptyMap()
    ): AiFlightAssessment {
        val es = language == AppLanguage.SPANISH
        fun t(en: String, esText: String) = if (es) esText else en

        // Production passes the same pure outcome used by the turn coordinator.
        // No random reward is assumed and heritage has no arbitrary ranking bonus.
        fun score(opt: CrisisOption): Int {
            val outcome = outcomes[opt.id]
            val after = outcome?.resources
            val energy = after?.let { it.energy - resources.energy } ?: opt.energyDelta
            val water = after?.let { it.water - resources.water } ?: opt.waterDelta
            val oxygen = after?.let { it.oxygen - resources.oxygen } ?: opt.oxygenDelta
            val food = after?.let { it.foodDays - resources.foodDays } ?: opt.foodDelta
            val health = after?.let { it.crewHealth - resources.crewHealth } ?: opt.healthDelta
            val morale = after?.let { it.morale - resources.morale } ?: opt.moraleDelta
            var s = 0
            s += energy + water + oxygen + food * 3 + health * 2 + morale
            s += (outcome?.averageSleepChange ?: opt.sleepDelta) - (outcome?.averageStressChange ?: opt.stressDelta)
            s += outcome?.scienceGain ?: 0
            // Prefer stabilising low resources
            if (resources.oxygen < 45) s += oxygen * 2
            if (resources.water < 45) s += water * 2
            if (resources.energy < 45) s += energy * 2
            if (resources.morale < 45) s += morale * 2
            if (resources.crewHealth < 50) s += health * 3
            if (after != null && (after.oxygen <= 0 || after.water <= 0 || after.foodDays <= 0 || after.crewHealth <= 0)) s -= 1000
            return s
        }

        val ranked = crisis.options.map { it to score(it) }.sortedByDescending { it.second }
        val best = ranked.firstOrNull()?.first
        val analysis = best?.let {
            t("Recommended: ${it.text}", "Recomendación: ${it.text}")
        } ?: t("No options available.", "No hay opciones disponibles.")

        val worstCase = when {
            resources.oxygen < 35 -> t(
                "Low oxygen: check depletion warnings.",
                "Oxígeno bajo: revisa los avisos de agotamiento."
            )
            resources.water < 35 -> t(
                "Low water: check depletion warnings.",
                "Agua baja: revisa los avisos de agotamiento."
            )
            resources.energy < 30 -> t(
                "Low energy: prioritise power reserves.",
                "Energía baja: prioriza la reserva eléctrica."
            )
            else -> ""
        }

        return AiFlightAssessment(
            title = t("Houston AI flight pass", "Pase de vuelo IA Houston"),
            analysis = analysis,
            worstCaseScenario = worstCase,
            recommendedOptionId = best?.id
        )
    }

    /**
     * Creates the initial crew with stats calibrated according to difficulty.
     */
    /**
     * Historical astronaut pool for training immersion.
     * Two random legends are assigned each mission (plus optional player name).
     */
    private data class HistoricalAstronaut(
        val name: String,
        val roleEn: String,
        val roleEs: String,
        val bioEn: String,
        val bioEs: String
    )

    /**
     * NASA + ESA astronauts only (including many women).
     * Two random legends are assigned each mission (plus optional player trainee).
     */
    private val historicalAstronauts = listOf(
        // NASA
        HistoricalAstronaut("Neil Armstrong", "Commander", "Comandante", "Apollo 11 commander; first human on the Moon (1969).", "Comandante del Apolo 11; primer humano en la Luna (1969)."),
        HistoricalAstronaut("Buzz Aldrin", "Lunar Module Pilot", "Piloto del módulo lunar", "Apollo 11; second human on the Moon.", "Apolo 11; segundo humano en la Luna."),
        HistoricalAstronaut("Sally Ride", "Mission Specialist", "Especialista de misión", "First American woman in space (STS-7, 1983).", "Primera mujer estadounidense en el espacio (STS-7, 1983)."),
        HistoricalAstronaut("Mae Jemison", "Mission Specialist", "Especialista de misión", "First Black woman in space (STS-47, 1992).", "Primera mujer negra en el espacio (STS-47, 1992)."),
        HistoricalAstronaut("Kalpana Chawla", "Mission Specialist", "Especialista de misión", "Engineer and Columbia mission specialist.", "Ingeniera y especialista de misión en Columbia."),
        HistoricalAstronaut("Peggy Whitson", "ISS Commander", "Comandante de la ISS", "NASA record for cumulative time in space; ISS commander.", "Récord NASA de tiempo en el espacio; comandante de la ISS."),
        HistoricalAstronaut("Jessica Meir", "Flight Engineer", "Ingeniera de vuelo", "NASA astronaut; ISS expeditions and spacewalks.", "Astronauta NASA; expediciones ISS y paseos espaciales."),
        HistoricalAstronaut("Christina Koch", "Flight Engineer", "Ingeniera de vuelo", "Record all-female spacewalk; Artemis generation.", "Paseo espacial récord solo femenino; generación Artemis."),
        HistoricalAstronaut("Sunita Williams", "ISS Commander", "Comandante de la ISS", "NASA astronaut; long-duration ISS commander and spacewalker.", "Astronauta NASA; comandante ISS y caminante espacial."),
        HistoricalAstronaut("Kathleen Rubins", "Flight Engineer", "Ingeniera de vuelo", "First person to sequence DNA in space.", "Primera persona en secuenciar ADN en el espacio."),
        HistoricalAstronaut("Nicole Mann", "Spacecraft Commander", "Comandante de nave", "First Indigenous woman from NASA in space; Crew-5 commander.", "Primera mujer indígena de la NASA en el espacio; comandante Crew-5."),
        HistoricalAstronaut("Jeanette Epps", "Mission Specialist", "Especialista de misión", "NASA astronaut; ISS expedition flight engineer.", "Astronauta NASA; ingeniera de vuelo en expediciones ISS."),
        HistoricalAstronaut("Michael Collins", "Command Module Pilot", "Piloto del módulo de mando", "Apollo 11 CMP; orbited the Moon alone.", "Piloto del módulo de mando Apolo 11; orbitó la Luna solo."),
        HistoricalAstronaut("John Glenn", "Pilot", "Piloto", "First American to orbit Earth (Mercury-Atlas 6).", "Primer estadounidense en orbitar la Tierra."),
        // ESA
        HistoricalAstronaut("Pedro Duque", "Mission Specialist", "Especialista de misión", "First Spanish astronaut (STS-95; Cervantes ISS). ESA.", "Primer astronauta español (STS-95; Cervantes ISS). ESA."),
        HistoricalAstronaut("Samantha Cristoforetti", "ISS Commander", "Comandante de la ISS", "ESA astronaut; ISS commander; long-duration flight records.", "Astronauta ESA; comandante ISS; récords de vuelo de larga duración."),
        HistoricalAstronaut("Thomas Pesquet", "ISS Commander", "Comandante de la ISS", "ESA astronaut; ISS commander (Proxima & Alpha).", "Astronauta ESA; comandante ISS (Proxima y Alpha)."),
        HistoricalAstronaut("Tim Peake", "Flight Engineer", "Ingeniero de vuelo", "British ESA astronaut; Principia ISS mission.", "Astronauta británico de la ESA; misión Principia."),
        HistoricalAstronaut("Luca Parmitano", "ISS Commander", "Comandante de la ISS", "ESA astronaut; ISS commander; emergency EVA water leak survival.", "Astronauta ESA; comandante ISS; supervivencia en EVA con fuga de agua."),
        HistoricalAstronaut("Claudie Haigneré", "Research Astronaut", "Astronauta investigadora", "First French woman in space; ESA/CNES research flights.", "Primera mujer francesa en el espacio; vuelos ESA/CNES."),
        HistoricalAstronaut("Eileen Collins", "Shuttle Commander", "Comandante del transbordador", "First woman to pilot and command a Space Shuttle.", "Primera mujer en pilotar y comandar un transbordador espacial."),
        HistoricalAstronaut("Ellen Ochoa", "Mission Specialist", "Especialista de misión", "First Latina astronaut; later NASA Johnson director.", "Primera astronauta latina; después directora del centro Johnson."),
        HistoricalAstronaut("Matthias Maurer", "Flight Engineer", "Ingeniero de vuelo", "German ESA astronaut; Cosmic Kiss ISS mission.", "Astronauta alemán de la ESA; misión Cosmic Kiss."),
        HistoricalAstronaut("Alexander Gerst", "ISS Commander", "Comandante de la ISS", "German ESA astronaut; ISS commander; Horizons mission.", "Astronauta alemán ESA; comandante ISS; misión Horizons."),
        HistoricalAstronaut("Andreas Mogensen", "ISS Commander", "Comandante de la ISS", "First Danish ESA astronaut; Huginn ISS command.", "Primer astronauta danés de la ESA; comando Huginn."),
        HistoricalAstronaut("Jessica Watkins", "Mission Specialist", "Especialista de misión", "NASA geologist astronaut; ISS long-duration crew.", "Astronauta geóloga de la NASA; tripulación larga en la ISS.")
    )

    fun createInitialCrew(
        difficulty: Difficulty = Difficulty.JUNIOR,
        playerName: String = "",
        language: AppLanguage = AppLanguage.ENGLISH,
        priority: PriorityPlan = PriorityPlan.CREW,
        habitat: HabitatPlan = HabitatPlan.STANDARD,
        random: Random = Random.Default
    ): List<CrewMember> {
        val initialSleep = when (difficulty) {
            Difficulty.JUNIOR -> 95
            Difficulty.MID -> 80
            Difficulty.SENIOR -> 70
        }
        val initialStress = when (difficulty) {
            Difficulty.JUNIOR -> 10
            Difficulty.MID -> 25
            Difficulty.SENIOR -> 40
        }
        val initialStatus = when (difficulty) {
            Difficulty.JUNIOR -> 98
            Difficulty.MID -> 88
            Difficulty.SENIOR -> 80
        }

        val design = priorityEffects(priority) + habitatEffects(habitat)
        val initialSleepAdj = initialSleep + design.sleep
        val initialStressAdj = initialStress + design.stress
        val healthBonus = design.health
        fun initialHealth(seed: Int, spread: Int): Int {
            val width = spread * 2 + 1
            val variation = ((seed % width + width) % width) - spread
            return (initialStatus + variation + healthBonus).coerceIn(0, 100)
        }

        val spanish = language == AppLanguage.SPANISH
        val count = if (playerName.trim().isEmpty()) 3 else 2
        val picked = historicalAstronauts.shuffled(random).take(count)
        // Small per-astronaut variation so health monitors look realistic
        fun vary(base: Int, spread: Int, seed: Int): Int {
            val width = spread * 2 + 1
            val delta = ((seed % width + width) % width) - spread
            return (base + delta).coerceIn(5, 100)
        }

        val legends = picked.mapIndexed { index, person ->
            val seed = person.name.hashCode() + index * 17
            CrewMember(
                id = "astro${index + 1}",
                name = person.name,
                sleep = vary(initialSleepAdj, 12, seed),
                stress = vary(initialStressAdj, 15, seed + 3),
                status = initialHealth(seed + 7, 10),
                bio = if (spanish) person.bioEs else person.bioEn,
                role = if (spanish) person.roleEs else person.roleEn
            )
        }

        val crew = legends.toMutableList()
        val trimmed = playerName.trim()
        if (trimmed.isNotEmpty()) {
            val seed = trimmed.hashCode()
            crew.add(
                0,
                CrewMember(
                    id = "player",
                    name = trimmed,
                    sleep = vary(initialSleepAdj, 10, seed),
                    stress = vary(initialStressAdj, 12, seed + 5),
                    status = initialHealth(seed + 9, 8),
                    bio = if (spanish) "Miembro de la tripulación de entrenamiento" else "Training crew member",
                    role = if (spanish) "Tripulante en prácticas" else "Trainee Astronaut"
                )
            )
        }
        return crew
    }

    fun createInitialResources(
        crew: List<CrewMember>,
        difficulty: Difficulty = Difficulty.JUNIOR,
        plan: MissionPlan = MissionPlan(),
        totalMissionDays: Int = 21
    ): Resources {
        require(crew.isNotEmpty()) { "A mission requires a crew" }
        // Food stocked for the planned stay + contingency margin (so the clock alone does not force 0)
        val contingency = when (difficulty) {
            Difficulty.JUNIOR -> 10
            Difficulty.MID -> 6
            Difficulty.SENIOR -> 3
        }
        val plannedFood = (totalMissionDays + contingency).coerceAtLeast(12)

        val base = when (difficulty) {
            Difficulty.JUNIOR -> Resources(
                energy = 100, water = 100, oxygen = 100,
                foodDays = plannedFood, morale = 95
            )
            Difficulty.MID -> Resources(
                energy = 90, water = 90, oxygen = 90,
                foodDays = plannedFood, morale = 85
            )
            Difficulty.SENIOR -> Resources(
                energy = 80, water = 80, oxygen = 82,
                foodDays = plannedFood, morale = 75
            )
        }

        // Sum choices before clamping, so costs cannot disappear behind an earlier cap.
        val design = plan.designEffects()
        return base.copy(
            energy = (base.energy + design.energy).coerceIn(0, 100),
            water = (base.water + design.water).coerceIn(0, 100),
            oxygen = (base.oxygen + design.oxygen).coerceIn(0, 100),
            foodDays = (base.foodDays + design.food).coerceAtLeast(0),
            crewHealth = aggregateCrewHealth(crew),
            morale = (base.morale + design.morale).coerceIn(0, 100)
        )
    }

    /** Describes actual rules. Narrative remains ES / EN, as in other catalog content. */
    fun worstCaseHints(plan: MissionPlan, language: AppLanguage): List<String> {
        val es = language == AppLanguage.SPANISH
        val texts = Strings.get(language)
        fun tr(en: String, spanish: String) = if (es) spanish else en
        fun signed(value: Int) = if (value > 0) "+$value" else "$value"
        fun initial(effects: DesignEffects): String {
            val values = listOf(
                texts.energy to effects.energy, texts.water to effects.water,
                texts.oxygen to effects.oxygen, texts.morale to effects.morale,
                texts.crewHealth to effects.health, texts.sleep to effects.sleep, texts.stress to effects.stress
            ).filter { it.second != 0 }.map { (name, value) -> "$name ${signed(value)} pp" }.toMutableList()
            if (effects.food != 0) values += "${texts.food} ${signed(effects.food)} ${texts.daysUnit}"
            return tr("Initial nominal changes (indicators capped at 0–100): ", "Ajustes iniciales nominales (indicadores limitados a 0–100): ") +
                values.joinToString("; ").ifBlank { tr("none", "ninguno") }
        }
        val power = powerEffects(plan.power)
        val habitat = habitatEffects(plan.habitat)
        val priority = priorityEffects(plan.priority)
        val normal = powerGeneration(plan.power, "")
        val disrupted = powerGeneration(plan.power, "dust_storm_filter")
        val cooling = power.waterPerCrisis
        val powerHint = tr("Power", "Energía") + ": " + initial(power) + ". " +
            tr("Recharge", "Recarga") + " +$normal pp/" + tr("crisis", "crisis") +
            tr(" (+$disrupted during dust / solar storms)", " (+$disrupted con polvo / tormenta solar)") +
            (if (cooling > 0) tr("; cooling uses $cooling extra water pp/crisis.", "; refrigeración: $cooling pp extra de agua/crisis.") else ".")
        return listOf(
            powerHint,
            tr("Habitat", "Hábitat") + ": " + initial(habitat) + ". " +
                tr("Water/O₂ use adjustment", "Ajuste de consumo agua/O₂") +
                " ${signed(habitat.waterPerCrisis)}/${signed(habitat.oxygenPerCrisis)} pp/" + tr("crisis", "crisis") +
                tr("; energy upkeep", "; mantenimiento energético") + " ${habitat.energyPerCrisis} pp/" + tr("crisis", "crisis") + ".",
            tr("Priority", "Prioridad") + ": " + initial(priority) + ". " +
                "${texts.sciencePoints}: ${sciencePerCrisis(plan.priority)}/" + tr("crisis", "crisis") +
                tr(" before decision adjustments. Science raises the score; it is not required to survive.",
                    " antes de los ajustes de la decisión. La ciencia mejora la nota; no es requisito para sobrevivir."),
            if (plan.planHardwareReuse)
                tr("Reuse enabled", "Reutilización activada") + ": " + initial(reuseEffects(true)) + ". " +
                    tr("Unlocks heritage options; no automatic crew recovery.", "Habilita opciones históricas; no recupera a la tripulación automáticamente.")
            else tr("Reuse disabled: no preparation cost; heritage options are unavailable.",
                "Reutilización desactivada: sin coste de preparación; las opciones históricas no están disponibles.")
        )
    }

    fun randomGoodNews(language: AppLanguage, random: Random = Random.Default): GoodNews =
        goodNewsCatalog(language).random(random)

    /** Read-only catalog for translating an already chosen event without drawing again. */
    fun goodNewsCatalog(language: AppLanguage): List<GoodNews> {
        val es = language == AppLanguage.SPANISH
        fun t(en: String, esText: String) = if (es) esText else en

        return listOf(
            com.worstcase.outpost.domain.GoodNews(
                id = "apollo13_power",
                title = t("Improvised procedure works", "Procedimiento improvisado funciona"),
                body = t("Reserve power protocol stabilizes vital telemetry.", "El protocolo de energía de reserva estabiliza la telemetría vital."),
                historicalNote = t(
                    "Apollo 13 (1970): after the SM O₂ tank explosion, Houston and the crew improvised power rationing and a CO₂ scrubber adapter (tape, bags, manual covers).",
                    "Apolo 13 (1970): tras la explosión del tanque de O₂, Houston y la tripulación improvisaron racionamiento de energía y un adaptador de CO₂ (cinta, bolsas, portadas del manual)."
                ),
                sleepDelta = 3, stressDelta = -8, statusDelta = 6, moraleDelta = 8, energyDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "soyuz_dock",
                title = t("Docking confirmed", "Acoplamiento confirmado"),
                body = t("Stable docking brings spares, water and contingency food.", "El acoplamiento estable trae repuestos, agua y comida de contingencia."),
                historicalNote = t(
                    "Progress/Soyuz cargo to the ISS has resupplied water, food and parts for decades — the backbone of long-duration logistics.",
                    "Progress/Soyuz hacia la ISS reabastecen agua, comida y piezas desde hace décadas: base de la logística de larga duración."
                ),
                waterDelta = 8, foodDelta = 2, stressDelta = -5, moraleDelta = 7, statusDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "hubble_repair",
                title = t("Successful EVA repair", "Reparación EVA exitosa"),
                body = t("A short EVA restores a faulty connector to nominal.", "Una EVA breve deja un conector defectuoso en modo nominal."),
                historicalNote = t(
                    "Hubble servicing (1993–2009): EVA crews replaced optics and gyros, recovering a flagship observatory after a flawed primary mirror.",
                    "Servicio del Hubble (1993–2009): las EVA reemplazaron óptica y giroscopios y recuperaron un observatorio con el espejo primario defectuoso."
                ),
                sleepDelta = 2, stressDelta = -7, statusDelta = 7, moraleDelta = 9
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "skylab_exercise",
                title = t("Exercise routine completed", "Rutina de ejercicio cumplida"),
                body = t("Prescribed exercise improves sleep and status.", "El ejercicio prescrito mejora sueño y estado."),
                historicalNote = t(
                    "Skylab and ISS daily exercise (treadmill, bike, resistance) limits bone and muscle loss in microgravity.",
                    "En Skylab e ISS el ejercicio diario (cinta, bicicleta, resistencia) limita la pérdida ósea y muscular en microgravedad."
                ),
                sleepDelta = 6, stressDelta = -5, statusDelta = 8, moraleDelta = 6
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "voyager_signal",
                title = t("Data link recovered", "Enlace de datos recuperado"),
                body = t("Antenna re-pointing restores a stable ground link.", "La reorientación de antena restaura un enlace estable."),
                historicalNote = t(
                    "Voyager 1/2 (1977–) still contact Earth; antenna pointing and power budgets keep deep-space links alive.",
                    "Voyager 1/2 (1977–) siguen contactando la Tierra; el apuntamiento y el presupuesto de potencia mantienen el enlace."
                ),
                stressDelta = -6, moraleDelta = 7, statusDelta = 3
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "apollo8_broadcast",
                title = t("Support message from Earth", "Mensaje de apoyo desde la Tierra"),
                body = t("Family and ground messages lift crew morale.", "Mensajes de familias y tierra elevan la moral."),
                historicalNote = t(
                    "Apollo 8 (1968) Christmas broadcast from lunar orbit linked the crew with millions on Earth.",
                    "Apolo 8 (1968) emitió en Nochebuena desde órbita lunar y conectó a la tripulación con millones de personas."
                ),
                sleepDelta = 2, stressDelta = -4, statusDelta = 3, moraleDelta = 10
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "sts_repair",
                title = t("Heat-shield inspection OK", "Inspección de escudo térmico OK"),
                body = t("No critical thermal-protection damage found.", "No se encuentran daños críticos de protección térmica."),
                historicalNote = t(
                    "Post-Columbia (2003), Shuttle on-orbit TPS inspections became standard before entry.",
                    "Tras Columbia (2003), las inspecciones TPS en órbita del Shuttle pasaron a ser estándar antes de la reentrada."
                ),
                stressDelta = -9, statusDelta = 5, moraleDelta = 8, sleepDelta = 3
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "iss_water_recycle",
                title = t("Water recycling above plan", "Reciclaje de agua por encima de lo previsto"),
                body = t("ECLSS recovery beats the plan; water margin improves.", "La recuperación del ECLSS supera el plan; mejora el margen de agua."),
                historicalNote = t(
                    "ISS water recovery turns urine and humidity into potable water, cutting resupply mass.",
                    "En la ISS, la recuperación de agua convierte orina y humedad en agua potable y reduce masa de reabastecimiento."
                ),
                waterDelta = 10, stressDelta = -3, moraleDelta = 5, statusDelta = 2
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "artemis1_splashdown",
                title = t("Recovery forces on station", "Fuerzas de recuperación en estación"),
                body = t("Ships report green status on the return corridor.", "Los buques reportan estado verde en el corredor de regreso."),
                historicalNote = t(
                    "Artemis I (2022): uncrewed Orion flew around the Moon and splashed down in the Pacific on 11 Dec 2022, proving heat shield and recovery ops.",
                    "Artemis I (2022): Orion sin tripulación orbitó la Luna y amerizó en el Pacífico el 11 dic 2022, validando escudo térmico y recuperación."
                ),
                stressDelta = -5, moraleDelta = 8, statusDelta = 4, sleepDelta = 2
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "mercury_friendship7",
                title = t("Re-entry corridor locked", "Corredor de reentrada fijado"),
                body = t("Guidance locks a safe corridor; recovery assets move.", "La guía fija un corredor seguro; se mueven medios de recuperación."),
                historicalNote = t(
                    "Friendship 7 (John Glenn, 1962) completed three orbits and a successful Atlantic recovery.",
                    "Friendship 7 (John Glenn, 1962) completó tres órbitas y una recuperación atlántica con éxito."
                ),
                stressDelta = -4, moraleDelta = 6, statusDelta = 3
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "apollo11_return",
                title = t("Sample container sealed", "Contenedor de muestras sellado"),
                body = t("Science samples secured; return timeline holds.", "Muestras aseguradas; se mantiene la cronología de regreso."),
                historicalNote = t(
                    "Apollo 11 returned sealed lunar samples; protecting them through entry was as critical as the landing.",
                    "Apolo 11 trajo muestras lunares selladas; protegerlas en la reentrada fue tan crítico como el alunizaje."
                ),
                sleepDelta = 3, stressDelta = -3, moraleDelta = 7, statusDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "cassini_gravity",
                title = t("Trajectory correction successful", "Corrección de trayectoria exitosa"),
                body = t("A planned burn trims the path within propellant rules.", "Una maniobra ajusta la ruta dentro de las reglas de propelente."),
                historicalNote = t(
                    "Cassini–Huygens used trajectory corrections and gravity assists to reach Saturn (2004).",
                    "Cassini–Huygens usó correcciones y asistencias gravitatorias para llegar a Saturno (2004)."
                ),
                stressDelta = -3, moraleDelta = 5, statusDelta = 2
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "gemini_rendezvous",
                title = t("Rendezvous practice nominal", "Práctica de rendezvous nominal"),
                body = t("Relative navigation agrees; practice approach is clean.", "La navegación relativa coincide; la aproximación de práctica es limpia."),
                historicalNote = t(
                    "Gemini (1965–66) proved orbital rendezvous and docking for Apollo and later ISS ops.",
                    "Gemini (1965–66) demostró rendezvous y acoplamiento para Apolo y más tarde la ISS."
                ),
                stressDelta = -5, moraleDelta = 6, statusDelta = 3, sleepDelta = 2
            ),
            // --- expanded set (more variety) ---
            com.worstcase.outpost.domain.GoodNews(
                id = "perseverance_oxy",
                title = t("ISRU oxygen demo nominal", "Demo ISRU de oxígeno nominal"),
                body = t("A brief oxygen-generation run meets test goals.", "Una prueba breve de generación de oxígeno cumple objetivos."),
                historicalNote = t(
                    "MOXIE on Perseverance (Mars 2020) produced oxygen from Martian CO₂ — a pathfinder for future crew ISRU.",
                    "MOXIE en Perseverance (Marte 2020) produjo oxígeno a partir de CO₂ marciano: precursor del ISRU para tripulaciones."
                ),
                statusDelta = 3, moraleDelta = 6, stressDelta = -3, oxygenDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "curiosity_sol",
                title = t("Long-duration systems healthy", "Sistemas de larga duración sanos"),
                body = t("Telemetry shows power and thermal within family.", "La telemetría muestra potencia y térmica dentro de familia."),
                historicalNote = t(
                    "Curiosity (MSL, 2012–) has operated many years on Mars with an RTG, showing long-surface power discipline.",
                    "Curiosity (MSL, 2012–) lleva años en Marte con RTG, ejemplo de disciplina energética en superficie."
                ),
                stressDelta = -2, moraleDelta = 4, statusDelta = 2
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "sts_hubble_sm3b",
                title = t("Redundant unit online", "Unidad redundante en línea"),
                body = t("A spare unit takes over; primary path is isolated.", "Una unidad de reserva toma el control; se aísla la vía primaria."),
                historicalNote = t(
                    "Hubble SM3B (2002) among other tasks installed a new power control unit — complex EVA logistics on a precision observatory.",
                    "Hubble SM3B (2002) instaló entre otras cosas una nueva unidad de control de potencia: EVA compleja en un observatorio de precisión."
                ),
                stressDelta = -4, moraleDelta = 5, statusDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "apollo_lm_lifeboat",
                title = t("Lifeboat mode stable", "Modo bote salvavidas estable"),
                body = t("Consumables in contingency mode track the plan.", "Los consumibles en modo contingencia siguen el plan."),
                historicalNote = t(
                    "Apollo 13 used the lunar module as a lifeboat, stretching power, water and lithium hydroxide beyond design assumptions.",
                    "Apolo 13 usó el módulo lunar como bote salvavidas, estirando potencia, agua y hidróxido de litio más allá del diseño."
                ),
                waterDelta = 3, stressDelta = -6, moraleDelta = 5, statusDelta = 3
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "iss_ammonia",
                title = t("Thermal loop recovered", "Bucle térmico recuperado"),
                body = t("External thermal control returns toward nominal flow.", "El control térmico externo vuelve hacia flujo nominal."),
                historicalNote = t(
                    "ISS has faced ammonia coolant loop issues; careful valve and pump management protected avionics and crew comfort.",
                    "La ISS ha tenido incidencias en bucles de amoníaco; la gestión de válvulas y bombas protegió aviónica y confort."
                ),
                stressDelta = -4, statusDelta = 3, moraleDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "sputnik_track",
                title = t("Tracking network solid", "Red de seguimiento sólida"),
                body = t("Ground stations hand over cleanly around the pass.", "Las estaciones de tierra hacen handovers limpios en el pase."),
                historicalNote = t(
                    "From Sputnik tracking to today’s DSN, continuous contact depends on a coordinated global ground network.",
                    "Desde el seguimiento de Sputnik hasta la DSN actual, el contacto continuo depende de una red global coordinada."
                ),
                stressDelta = -2, moraleDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "apollo15_lrvc",
                title = t("Mobility aid restored", "Ayuda a la movilidad restaurada"),
                body = t("Field repair restores surface traverse capability.", "Una reparación de campo restaura la capacidad de travesía."),
                historicalNote = t(
                    "Apollo LRV fender repairs with maps and tape kept dust under control and protected suits and cameras.",
                    "Las reparaciones del guardabarros del LRV Apolo con mapas y cinta controlaron el polvo y protegieron trajes y cámaras."
                ),
                stressDelta = -3, moraleDelta = 5, statusDelta = 2, sleepDelta = 1
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "orion_eclss",
                title = t("Cabin atmosphere steady", "Atmósfera de cabina estable"),
                body = t("ppO₂ and CO₂ stay inside crew limits.", "ppO₂ y CO₂ se mantienen dentro de límites de tripulación."),
                historicalNote = t(
                    "Orion ECLSS development for Artemis targets reliable cabin atmosphere for lunar missions and recovery.",
                    "El ECLSS de Orion para Artemis busca una atmósfera de cabina fiable en misiones lunares y recuperación."
                ),
                stressDelta = -3, statusDelta = 4, moraleDelta = 4
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "mars_insight",
                title = t("Quiet period for maintenance", "Periodo tranquilo para mantenimiento"),
                body = t("A low-activity window allows thorough system checks.", "Una ventana de baja actividad permite chequeos a fondo."),
                historicalNote = t(
                    "InSight (2018–2022) showed how dust and power margins drive ops tempo on Mars; quiet sols were used for health checks.",
                    "InSight (2018–2022) mostró cómo polvo y márgenes de potencia marcan el ritmo en Marte; los soles tranquilos servían para chequeos."
                ),
                sleepDelta = 4, stressDelta = -4, statusDelta = 3, moraleDelta = 3
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "esa_atv",
                title = t("Logistics module secured", "Módulo logístico asegurado"),
                body = t("Cargo transfer plan is confirmed with ground.", "El plan de transferencia de carga se confirma con tierra."),
                historicalNote = t(
                    "ESA’s ATV freighters delivered propellant, water and dry cargo to the ISS before retiring after five missions.",
                    "Los ATV de la ESA llevaron propelente, agua y carga seca a la ISS antes de retirarse tras cinco misiones."
                ),
                waterDelta = 5, foodDelta = 1, moraleDelta = 5, stressDelta = -2
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "apollo_seismic",
                title = t("Science priority window open", "Ventana prioritaria de ciencia abierta"),
                body = t("A short science pass completes without resource conflict.", "Una pasada científica breve se completa sin conflicto de recursos."),
                historicalNote = t(
                    "Apollo lunar seismic experiments returned data for years, showing surface science can outlive short crew visits.",
                    "Los experimentos sísmicos Apolo devolvieron datos años: la ciencia de superficie puede durar más que la visita tripulada."
                ),
                moraleDelta = 5, stressDelta = -2, statusDelta = 2
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "crew_dragon",
                title = t("Abort system checks green", "Chequeos del sistema de aborto en verde"),
                body = t("Launch-escape / abort readiness is verified.", "Se verifica la disposición de escape / aborto."),
                historicalNote = t(
                    "Modern crew vehicles (e.g. Crew Dragon) emphasize launch-escape capability after lessons from earlier programs.",
                    "Los vehículos tripulados modernos (p. ej. Crew Dragon) priorizan escape en lanzamiento tras lecciones de programas previos."
                ),
                stressDelta = -5, moraleDelta = 6, statusDelta = 3
            ),
            com.worstcase.outpost.domain.GoodNews(
                id = "lunar_gateway_plan",
                title = t("Relay coverage improved", "Cobertura de retransmisión mejorada"),
                body = t("Comm geometry yields a longer high-rate window.", "La geometría de coms da una ventana de alta tasa más larga."),
                historicalNote = t(
                    "Artemis architecture plans Gateway and relay assets so lunar crews are not limited to direct-to-Earth line of sight.",
                    "La arquitectura Artemis prevé Gateway y reles para que las tripulaciones lunares no dependan solo de la línea de vista a Tierra."
                ),
                stressDelta = -3, moraleDelta = 5, statusDelta = 2
            )
        )
    }




}

