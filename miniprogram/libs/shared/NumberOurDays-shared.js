(function (_, kotlin_kotlin, kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core, kotlin_org_jetbrains_kotlinx_kotlinx_datetime, kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json) {
  'use strict';
  //region block: imports
  var imul = Math.imul;
  var replace = kotlin_kotlin.$_$.tb;
  var protoOf = kotlin_kotlin.$_$.fa;
  var initMetadataForObject = kotlin_kotlin.$_$.l9;
  var initMetadataForCompanion = kotlin_kotlin.$_$.h9;
  var PluginGeneratedSerialDescriptor = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.v1;
  var StringSerializer_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.o;
  var IntSerializer_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.m;
  var System_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.f;
  var THROW_CCE = kotlin_kotlin.$_$.kd;
  var Long = kotlin_kotlin.$_$.fd;
  var UnknownFieldException_init_$Create$ = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.c;
  var get_nullable = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.r;
  var BooleanSerializer_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.l;
  var LongSerializer_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.n;
  var typeParametersSerializers = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.q1;
  var GeneratedSerializer = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.r1;
  var VOID = kotlin_kotlin.$_$.e;
  var throwMissingFieldException = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.y1;
  var objectCreate = kotlin_kotlin.$_$.ea;
  var getStringHashCode = kotlin_kotlin.$_$.e9;
  var getBooleanHashCode = kotlin_kotlin.$_$.c9;
  var initMetadataForClass = kotlin_kotlin.$_$.g9;
  var charSequenceLength = kotlin_kotlin.$_$.w8;
  var coerceAtLeast = kotlin_kotlin.$_$.na;
  var coerceIn = kotlin_kotlin.$_$.pa;
  var isCharSequence = kotlin_kotlin.$_$.q9;
  var trim = kotlin_kotlin.$_$.oc;
  var toString = kotlin_kotlin.$_$.ja;
  var listOf = kotlin_kotlin.$_$.v6;
  var joinToString = kotlin_kotlin.$_$.o6;
  var Unit_getInstance = kotlin_kotlin.$_$.u4;
  var Json = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.e;
  var getKClass = kotlin_kotlin.$_$.d;
  var arrayOf = kotlin_kotlin.$_$.yd;
  var createKType = kotlin_kotlin.$_$.a;
  var serializer = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.m2;
  var KSerializer = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_core.$_$.e2;
  var isInterface = kotlin_kotlin.$_$.u9;
  var isBlank = kotlin_kotlin.$_$.ib;
  var Companion_getInstance = kotlin_kotlin.$_$.p4;
  var _Result___init__impl__xyqfz8 = kotlin_kotlin.$_$.m2;
  var createFailure = kotlin_kotlin.$_$.ae;
  var _Result___get_value__impl__bjfvqg = kotlin_kotlin.$_$.p2;
  var _Result___get_isFailure__impl__jpiriv = kotlin_kotlin.$_$.o2;
  var get_jsonArray = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.f;
  var ArrayList_init_$Create$ = kotlin_kotlin.$_$.k;
  var JsonObject = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.d;
  var Result__exceptionOrNull_impl_p6xea9 = kotlin_kotlin.$_$.n2;
  var emptyList = kotlin_kotlin.$_$.e6;
  var JsonObjectBuilder = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.c;
  var put = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.i;
  var put_0 = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.h;
  var JsonArrayBuilder = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.b;
  var Companion_getInstance_0 = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.a;
  var to = kotlin_kotlin.$_$.me;
  var get_jsonObject = kotlin_org_jetbrains_kotlinx_kotlinx_serialization_json.$_$.g;
  var Exception = kotlin_kotlin.$_$.cd;
  var LinkedHashMap_init_$Create$ = kotlin_kotlin.$_$.t;
  var HashMap_init_$Create$ = kotlin_kotlin.$_$.n;
  var toList = kotlin_kotlin.$_$.n7;
  var toString_0 = kotlin_kotlin.$_$.hc;
  var Default_getInstance = kotlin_kotlin.$_$.j4;
  var DayOfWeek_SATURDAY_getInstance = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.a;
  var DatePeriod_init_$Create$ = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.b;
  var plus = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.j;
  var Companion_getInstance_1 = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.i;
  var todayIn = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.m;
  var LocalDate_init_$Create$ = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.c;
  var initMetadataForInterface = kotlin_kotlin.$_$.j9;
  var THROW_IAE = kotlin_kotlin.$_$.ld;
  var enumEntries = kotlin_kotlin.$_$.o8;
  var Enum = kotlin_kotlin.$_$.ad;
  var noWhenBranchMatchedException = kotlin_kotlin.$_$.he;
  var equals = kotlin_kotlin.$_$.a9;
  var FunctionAdapter = kotlin_kotlin.$_$.p8;
  var Comparator = kotlin_kotlin.$_$.xc;
  var hashCode = kotlin_kotlin.$_$.f9;
  var compareValues = kotlin_kotlin.$_$.v7;
  var reversed = kotlin_kotlin.$_$.ub;
  var Companion_getInstance_2 = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.h;
  var Regex_init_$Create$ = kotlin_kotlin.$_$.a1;
  var toLong = kotlin_kotlin.$_$.ha;
  var numberToLong = kotlin_kotlin.$_$.da;
  var abs = kotlin_kotlin.$_$.ka;
  var sortedWith = kotlin_kotlin.$_$.k7;
  var compareTo = kotlin_kotlin.$_$.y8;
  var first = kotlin_kotlin.$_$.i6;
  var listOf_0 = kotlin_kotlin.$_$.u6;
  var plus_0 = kotlin_kotlin.$_$.b7;
  var abs_0 = kotlin_kotlin.$_$.la;
  var collectionSizeOrDefault = kotlin_kotlin.$_$.p5;
  var ArrayList_init_$Create$_0 = kotlin_kotlin.$_$.j;
  var LocalTime_init_$Create$ = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.e;
  var LocalDateTime_init_$Create$ = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.d;
  var toInstant = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.k;
  var Companion_getInstance_3 = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.g;
  var toLocalDateTime = kotlin_org_jetbrains_kotlinx_kotlinx_datetime.$_$.l;
  var startsWith = kotlin_kotlin.$_$.wb;
  var trimIndent = kotlin_kotlin.$_$.nc;
  var PrimitiveClasses_getInstance = kotlin_kotlin.$_$.k4;
  var defineProp = kotlin_kotlin.$_$.z8;
  //endregion
  //region block: pre-declaration
  initMetadataForObject(BackupNaming, 'BackupNaming');
  initMetadataForCompanion(Companion);
  initMetadataForObject($serializer, '$serializer', VOID, VOID, [GeneratedSerializer]);
  initMetadataForClass(DayEventDto, 'DayEventDto', DayEventDto, VOID, VOID, VOID, VOID, {0: $serializer_getInstance});
  initMetadataForObject(DayEventCodec, 'DayEventCodec');
  initMetadataForObject(DemoEvents, 'DemoEvents');
  initMetadataForInterface(EventRepository, 'EventRepository');
  initMetadataForClass(HolidayPeriod, 'HolidayPeriod');
  initMetadataForClass(HolidayStatus, 'HolidayStatus', VOID, Enum);
  initMetadataForClass(HolidayCountdown, 'HolidayCountdown');
  initMetadataForObject(LegalHolidays, 'LegalHolidays');
  initMetadataForCompanion(Companion_0);
  initMetadataForClass(RepeatUnit, 'RepeatUnit', VOID, Enum);
  initMetadataForClass(EventKind, 'EventKind', VOID, Enum);
  initMetadataForClass(DayEvent, 'DayEvent');
  initMetadataForClass(DayEventDisplay, 'DayEventDisplay');
  initMetadataForClass(DurationBreakdown, 'DurationBreakdown');
  initMetadataForClass(DurationDisplay, 'DurationDisplay');
  initMetadataForClass(DurationUnit, 'DurationUnit', VOID, Enum);
  initMetadataForClass(sam$kotlin_Comparator$0, 'sam$kotlin_Comparator$0', VOID, VOID, [Comparator, FunctionAdapter]);
  initMetadataForObject(DateCalculator, 'DateCalculator');
  initMetadataForClass(sam$kotlin_Comparator$0_0, 'sam$kotlin_Comparator$0', VOID, VOID, [Comparator, FunctionAdapter]);
  initMetadataForObject(HolidayCalculator, 'HolidayCalculator');
  initMetadataForClass(HolidayListRow, 'HolidayListRow');
  initMetadataForObject(ReminderTiming, 'ReminderTiming');
  initMetadataForObject(WeappDomain, 'WeappDomain');
  //endregion
  function BackupNaming() {
    BackupNaming_instance = this;
  }
  protoOf(BackupNaming).defaultBackupFileName_o4jrln_k$ = function (date) {
    var ymd = replace(date.toString(), '-', '');
    return 'numberourdays-backup-' + ymd + '.json';
  };
  var BackupNaming_instance;
  function BackupNaming_getInstance() {
    if (BackupNaming_instance == null)
      new BackupNaming();
    return BackupNaming_instance;
  }
  function Companion() {
    Companion_instance = this;
  }
  protoOf(Companion).serializer_9w0wvi_k$ = function () {
    return $serializer_getInstance();
  };
  var Companion_instance;
  function Companion_getInstance_4() {
    if (Companion_instance == null)
      new Companion();
    return Companion_instance;
  }
  function $serializer() {
    $serializer_instance = this;
    var tmp0_serialDesc = new PluginGeneratedSerialDescriptor('com.numberourdays.app.data.DayEventCodec.DayEventDto', this, 13);
    tmp0_serialDesc.addElement_5pzumi_k$('id', true);
    tmp0_serialDesc.addElement_5pzumi_k$('title', true);
    tmp0_serialDesc.addElement_5pzumi_k$('date', true);
    tmp0_serialDesc.addElement_5pzumi_k$('note', true);
    tmp0_serialDesc.addElement_5pzumi_k$('starred', true);
    tmp0_serialDesc.addElement_5pzumi_k$('starRank', true);
    tmp0_serialDesc.addElement_5pzumi_k$('repeatUnit', true);
    tmp0_serialDesc.addElement_5pzumi_k$('repeatInterval', true);
    tmp0_serialDesc.addElement_5pzumi_k$('remindEnabled', true);
    tmp0_serialDesc.addElement_5pzumi_k$('remindHour', true);
    tmp0_serialDesc.addElement_5pzumi_k$('remindMinute', true);
    tmp0_serialDesc.addElement_5pzumi_k$('createdAt', true);
    tmp0_serialDesc.addElement_5pzumi_k$('updatedAt', true);
    this.descriptor_1 = tmp0_serialDesc;
  }
  protoOf($serializer).serialize_3qz9u2_k$ = function (encoder, value) {
    var tmp0_desc = this.descriptor_1;
    var tmp1_output = encoder.beginStructure_yljocp_k$(tmp0_desc);
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 0) ? true : !(value.id_1 === '')) {
      tmp1_output.encodeStringElement_1n5wu2_k$(tmp0_desc, 0, value.id_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 1) ? true : !(value.title_1 === '')) {
      tmp1_output.encodeStringElement_1n5wu2_k$(tmp0_desc, 1, value.title_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 2) ? true : !(value.date_1 === '')) {
      tmp1_output.encodeStringElement_1n5wu2_k$(tmp0_desc, 2, value.date_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 3) ? true : !(value.note_1 == null)) {
      tmp1_output.encodeNullableSerializableElement_5lquiv_k$(tmp0_desc, 3, StringSerializer_getInstance(), value.note_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 4) ? true : !(value.starred_1 === false)) {
      tmp1_output.encodeBooleanElement_ydht7q_k$(tmp0_desc, 4, value.starred_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 5) ? true : !(value.starRank_1 == null)) {
      tmp1_output.encodeNullableSerializableElement_5lquiv_k$(tmp0_desc, 5, IntSerializer_getInstance(), value.starRank_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 6) ? true : !(value.repeatUnit_1 === 'none')) {
      tmp1_output.encodeStringElement_1n5wu2_k$(tmp0_desc, 6, value.repeatUnit_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 7) ? true : !(value.repeatInterval_1 === 1)) {
      tmp1_output.encodeIntElement_krhhce_k$(tmp0_desc, 7, value.repeatInterval_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 8) ? true : !(value.remindEnabled_1 === false)) {
      tmp1_output.encodeBooleanElement_ydht7q_k$(tmp0_desc, 8, value.remindEnabled_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 9) ? true : !(value.remindHour_1 === 9)) {
      tmp1_output.encodeIntElement_krhhce_k$(tmp0_desc, 9, value.remindHour_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 10) ? true : !(value.remindMinute_1 === 0)) {
      tmp1_output.encodeIntElement_krhhce_k$(tmp0_desc, 10, value.remindMinute_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 11) ? true : !value.createdAt_1.equals(System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$())) {
      tmp1_output.encodeLongElement_cega27_k$(tmp0_desc, 11, value.createdAt_1);
    }
    if (tmp1_output.shouldEncodeElementDefault_x8eyid_k$(tmp0_desc, 12) ? true : !value.updatedAt_1.equals(System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$())) {
      tmp1_output.encodeLongElement_cega27_k$(tmp0_desc, 12, value.updatedAt_1);
    }
    tmp1_output.endStructure_1xqz0n_k$(tmp0_desc);
  };
  protoOf($serializer).serialize_5ase3y_k$ = function (encoder, value) {
    return this.serialize_3qz9u2_k$(encoder, value instanceof DayEventDto ? value : THROW_CCE());
  };
  protoOf($serializer).deserialize_sy6x50_k$ = function (decoder) {
    var tmp0_desc = this.descriptor_1;
    var tmp1_flag = true;
    var tmp2_index = 0;
    var tmp3_bitMask0 = 0;
    var tmp4_local0 = null;
    var tmp5_local1 = null;
    var tmp6_local2 = null;
    var tmp7_local3 = null;
    var tmp8_local4 = false;
    var tmp9_local5 = null;
    var tmp10_local6 = null;
    var tmp11_local7 = 0;
    var tmp12_local8 = false;
    var tmp13_local9 = 0;
    var tmp14_local10 = 0;
    var tmp15_local11 = new Long(0, 0);
    var tmp16_local12 = new Long(0, 0);
    var tmp17_input = decoder.beginStructure_yljocp_k$(tmp0_desc);
    if (tmp17_input.decodeSequentially_xlblqy_k$()) {
      tmp4_local0 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 0);
      tmp3_bitMask0 = tmp3_bitMask0 | 1;
      tmp5_local1 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 1);
      tmp3_bitMask0 = tmp3_bitMask0 | 2;
      tmp6_local2 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 2);
      tmp3_bitMask0 = tmp3_bitMask0 | 4;
      tmp7_local3 = tmp17_input.decodeNullableSerializableElement_k2y6ab_k$(tmp0_desc, 3, StringSerializer_getInstance(), tmp7_local3);
      tmp3_bitMask0 = tmp3_bitMask0 | 8;
      tmp8_local4 = tmp17_input.decodeBooleanElement_vuyhtj_k$(tmp0_desc, 4);
      tmp3_bitMask0 = tmp3_bitMask0 | 16;
      tmp9_local5 = tmp17_input.decodeNullableSerializableElement_k2y6ab_k$(tmp0_desc, 5, IntSerializer_getInstance(), tmp9_local5);
      tmp3_bitMask0 = tmp3_bitMask0 | 32;
      tmp10_local6 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 6);
      tmp3_bitMask0 = tmp3_bitMask0 | 64;
      tmp11_local7 = tmp17_input.decodeIntElement_941u6a_k$(tmp0_desc, 7);
      tmp3_bitMask0 = tmp3_bitMask0 | 128;
      tmp12_local8 = tmp17_input.decodeBooleanElement_vuyhtj_k$(tmp0_desc, 8);
      tmp3_bitMask0 = tmp3_bitMask0 | 256;
      tmp13_local9 = tmp17_input.decodeIntElement_941u6a_k$(tmp0_desc, 9);
      tmp3_bitMask0 = tmp3_bitMask0 | 512;
      tmp14_local10 = tmp17_input.decodeIntElement_941u6a_k$(tmp0_desc, 10);
      tmp3_bitMask0 = tmp3_bitMask0 | 1024;
      tmp15_local11 = tmp17_input.decodeLongElement_994anb_k$(tmp0_desc, 11);
      tmp3_bitMask0 = tmp3_bitMask0 | 2048;
      tmp16_local12 = tmp17_input.decodeLongElement_994anb_k$(tmp0_desc, 12);
      tmp3_bitMask0 = tmp3_bitMask0 | 4096;
    } else
      while (tmp1_flag) {
        tmp2_index = tmp17_input.decodeElementIndex_bstkhp_k$(tmp0_desc);
        switch (tmp2_index) {
          case -1:
            tmp1_flag = false;
            break;
          case 0:
            tmp4_local0 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 0);
            tmp3_bitMask0 = tmp3_bitMask0 | 1;
            break;
          case 1:
            tmp5_local1 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 1);
            tmp3_bitMask0 = tmp3_bitMask0 | 2;
            break;
          case 2:
            tmp6_local2 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 2);
            tmp3_bitMask0 = tmp3_bitMask0 | 4;
            break;
          case 3:
            tmp7_local3 = tmp17_input.decodeNullableSerializableElement_k2y6ab_k$(tmp0_desc, 3, StringSerializer_getInstance(), tmp7_local3);
            tmp3_bitMask0 = tmp3_bitMask0 | 8;
            break;
          case 4:
            tmp8_local4 = tmp17_input.decodeBooleanElement_vuyhtj_k$(tmp0_desc, 4);
            tmp3_bitMask0 = tmp3_bitMask0 | 16;
            break;
          case 5:
            tmp9_local5 = tmp17_input.decodeNullableSerializableElement_k2y6ab_k$(tmp0_desc, 5, IntSerializer_getInstance(), tmp9_local5);
            tmp3_bitMask0 = tmp3_bitMask0 | 32;
            break;
          case 6:
            tmp10_local6 = tmp17_input.decodeStringElement_3oenpg_k$(tmp0_desc, 6);
            tmp3_bitMask0 = tmp3_bitMask0 | 64;
            break;
          case 7:
            tmp11_local7 = tmp17_input.decodeIntElement_941u6a_k$(tmp0_desc, 7);
            tmp3_bitMask0 = tmp3_bitMask0 | 128;
            break;
          case 8:
            tmp12_local8 = tmp17_input.decodeBooleanElement_vuyhtj_k$(tmp0_desc, 8);
            tmp3_bitMask0 = tmp3_bitMask0 | 256;
            break;
          case 9:
            tmp13_local9 = tmp17_input.decodeIntElement_941u6a_k$(tmp0_desc, 9);
            tmp3_bitMask0 = tmp3_bitMask0 | 512;
            break;
          case 10:
            tmp14_local10 = tmp17_input.decodeIntElement_941u6a_k$(tmp0_desc, 10);
            tmp3_bitMask0 = tmp3_bitMask0 | 1024;
            break;
          case 11:
            tmp15_local11 = tmp17_input.decodeLongElement_994anb_k$(tmp0_desc, 11);
            tmp3_bitMask0 = tmp3_bitMask0 | 2048;
            break;
          case 12:
            tmp16_local12 = tmp17_input.decodeLongElement_994anb_k$(tmp0_desc, 12);
            tmp3_bitMask0 = tmp3_bitMask0 | 4096;
            break;
          default:
            throw UnknownFieldException_init_$Create$(tmp2_index);
        }
      }
    tmp17_input.endStructure_1xqz0n_k$(tmp0_desc);
    return DayEventDto_init_$Create$(tmp3_bitMask0, tmp4_local0, tmp5_local1, tmp6_local2, tmp7_local3, tmp8_local4, tmp9_local5, tmp10_local6, tmp11_local7, tmp12_local8, tmp13_local9, tmp14_local10, tmp15_local11, tmp16_local12, null);
  };
  protoOf($serializer).get_descriptor_wjt6a0_k$ = function () {
    return this.descriptor_1;
  };
  protoOf($serializer).childSerializers_5ghqw5_k$ = function () {
    // Inline function 'kotlin.arrayOf' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    return [StringSerializer_getInstance(), StringSerializer_getInstance(), StringSerializer_getInstance(), get_nullable(StringSerializer_getInstance()), BooleanSerializer_getInstance(), get_nullable(IntSerializer_getInstance()), StringSerializer_getInstance(), IntSerializer_getInstance(), BooleanSerializer_getInstance(), IntSerializer_getInstance(), IntSerializer_getInstance(), LongSerializer_getInstance(), LongSerializer_getInstance()];
  };
  var $serializer_instance;
  function $serializer_getInstance() {
    if ($serializer_instance == null)
      new $serializer();
    return $serializer_instance;
  }
  function DayEventDto_init_$Init$(seen0, id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt, serializationConstructorMarker, $this) {
    if (!(0 === (0 & seen0))) {
      throwMissingFieldException(seen0, 0, $serializer_getInstance().descriptor_1);
    }
    if (0 === (seen0 & 1))
      $this.id_1 = '';
    else
      $this.id_1 = id;
    if (0 === (seen0 & 2))
      $this.title_1 = '';
    else
      $this.title_1 = title;
    if (0 === (seen0 & 4))
      $this.date_1 = '';
    else
      $this.date_1 = date;
    if (0 === (seen0 & 8))
      $this.note_1 = null;
    else
      $this.note_1 = note;
    if (0 === (seen0 & 16))
      $this.starred_1 = false;
    else
      $this.starred_1 = starred;
    if (0 === (seen0 & 32))
      $this.starRank_1 = null;
    else
      $this.starRank_1 = starRank;
    if (0 === (seen0 & 64))
      $this.repeatUnit_1 = 'none';
    else
      $this.repeatUnit_1 = repeatUnit;
    if (0 === (seen0 & 128))
      $this.repeatInterval_1 = 1;
    else
      $this.repeatInterval_1 = repeatInterval;
    if (0 === (seen0 & 256))
      $this.remindEnabled_1 = false;
    else
      $this.remindEnabled_1 = remindEnabled;
    if (0 === (seen0 & 512))
      $this.remindHour_1 = 9;
    else
      $this.remindHour_1 = remindHour;
    if (0 === (seen0 & 1024))
      $this.remindMinute_1 = 0;
    else
      $this.remindMinute_1 = remindMinute;
    if (0 === (seen0 & 2048))
      $this.createdAt_1 = System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$();
    else
      $this.createdAt_1 = createdAt;
    if (0 === (seen0 & 4096))
      $this.updatedAt_1 = System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$();
    else
      $this.updatedAt_1 = updatedAt;
    return $this;
  }
  function DayEventDto_init_$Create$(seen0, id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt, serializationConstructorMarker) {
    return DayEventDto_init_$Init$(seen0, id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt, serializationConstructorMarker, objectCreate(protoOf(DayEventDto)));
  }
  function _get_json__d8whur($this) {
    return $this.json_1;
  }
  function DayEventDto(id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt) {
    Companion_getInstance_4();
    id = id === VOID ? '' : id;
    title = title === VOID ? '' : title;
    date = date === VOID ? '' : date;
    note = note === VOID ? null : note;
    starred = starred === VOID ? false : starred;
    starRank = starRank === VOID ? null : starRank;
    repeatUnit = repeatUnit === VOID ? 'none' : repeatUnit;
    repeatInterval = repeatInterval === VOID ? 1 : repeatInterval;
    remindEnabled = remindEnabled === VOID ? false : remindEnabled;
    remindHour = remindHour === VOID ? 9 : remindHour;
    remindMinute = remindMinute === VOID ? 0 : remindMinute;
    createdAt = createdAt === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : createdAt;
    updatedAt = updatedAt === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : updatedAt;
    this.id_1 = id;
    this.title_1 = title;
    this.date_1 = date;
    this.note_1 = note;
    this.starred_1 = starred;
    this.starRank_1 = starRank;
    this.repeatUnit_1 = repeatUnit;
    this.repeatInterval_1 = repeatInterval;
    this.remindEnabled_1 = remindEnabled;
    this.remindHour_1 = remindHour;
    this.remindMinute_1 = remindMinute;
    this.createdAt_1 = createdAt;
    this.updatedAt_1 = updatedAt;
  }
  protoOf(DayEventDto).get_id_kntnx8_k$ = function () {
    return this.id_1;
  };
  protoOf(DayEventDto).get_title_iz32un_k$ = function () {
    return this.title_1;
  };
  protoOf(DayEventDto).get_date_wokkxj_k$ = function () {
    return this.date_1;
  };
  protoOf(DayEventDto).get_note_wor96j_k$ = function () {
    return this.note_1;
  };
  protoOf(DayEventDto).get_starred_u3zcve_k$ = function () {
    return this.starred_1;
  };
  protoOf(DayEventDto).get_starRank_a1jvkp_k$ = function () {
    return this.starRank_1;
  };
  protoOf(DayEventDto).get_repeatUnit_zunjs_k$ = function () {
    return this.repeatUnit_1;
  };
  protoOf(DayEventDto).get_repeatInterval_awuft5_k$ = function () {
    return this.repeatInterval_1;
  };
  protoOf(DayEventDto).get_remindEnabled_8nmteb_k$ = function () {
    return this.remindEnabled_1;
  };
  protoOf(DayEventDto).get_remindHour_muku0e_k$ = function () {
    return this.remindHour_1;
  };
  protoOf(DayEventDto).get_remindMinute_7gj5fi_k$ = function () {
    return this.remindMinute_1;
  };
  protoOf(DayEventDto).get_createdAt_ierzpu_k$ = function () {
    return this.createdAt_1;
  };
  protoOf(DayEventDto).get_updatedAt_npz717_k$ = function () {
    return this.updatedAt_1;
  };
  protoOf(DayEventDto).component1_7eebsc_k$ = function () {
    return this.id_1;
  };
  protoOf(DayEventDto).component2_7eebsb_k$ = function () {
    return this.title_1;
  };
  protoOf(DayEventDto).component3_7eebsa_k$ = function () {
    return this.date_1;
  };
  protoOf(DayEventDto).component4_7eebs9_k$ = function () {
    return this.note_1;
  };
  protoOf(DayEventDto).component5_7eebs8_k$ = function () {
    return this.starred_1;
  };
  protoOf(DayEventDto).component6_7eebs7_k$ = function () {
    return this.starRank_1;
  };
  protoOf(DayEventDto).component7_7eebs6_k$ = function () {
    return this.repeatUnit_1;
  };
  protoOf(DayEventDto).component8_7eebs5_k$ = function () {
    return this.repeatInterval_1;
  };
  protoOf(DayEventDto).component9_7eebs4_k$ = function () {
    return this.remindEnabled_1;
  };
  protoOf(DayEventDto).component10_gazzfo_k$ = function () {
    return this.remindHour_1;
  };
  protoOf(DayEventDto).component11_gazzfn_k$ = function () {
    return this.remindMinute_1;
  };
  protoOf(DayEventDto).component12_gazzfm_k$ = function () {
    return this.createdAt_1;
  };
  protoOf(DayEventDto).component13_gazzfl_k$ = function () {
    return this.updatedAt_1;
  };
  protoOf(DayEventDto).copy_aum0qz_k$ = function (id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt) {
    return new DayEventDto(id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt);
  };
  protoOf(DayEventDto).copy$default_u8qcs9_k$ = function (id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt, $super) {
    id = id === VOID ? this.id_1 : id;
    title = title === VOID ? this.title_1 : title;
    date = date === VOID ? this.date_1 : date;
    note = note === VOID ? this.note_1 : note;
    starred = starred === VOID ? this.starred_1 : starred;
    starRank = starRank === VOID ? this.starRank_1 : starRank;
    repeatUnit = repeatUnit === VOID ? this.repeatUnit_1 : repeatUnit;
    repeatInterval = repeatInterval === VOID ? this.repeatInterval_1 : repeatInterval;
    remindEnabled = remindEnabled === VOID ? this.remindEnabled_1 : remindEnabled;
    remindHour = remindHour === VOID ? this.remindHour_1 : remindHour;
    remindMinute = remindMinute === VOID ? this.remindMinute_1 : remindMinute;
    createdAt = createdAt === VOID ? this.createdAt_1 : createdAt;
    updatedAt = updatedAt === VOID ? this.updatedAt_1 : updatedAt;
    return $super === VOID ? this.copy_aum0qz_k$(id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt) : $super.copy_aum0qz_k$.call(this, id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt);
  };
  protoOf(DayEventDto).toString = function () {
    return 'DayEventDto(id=' + this.id_1 + ', title=' + this.title_1 + ', date=' + this.date_1 + ', note=' + this.note_1 + ', starred=' + this.starred_1 + ', starRank=' + this.starRank_1 + ', repeatUnit=' + this.repeatUnit_1 + ', repeatInterval=' + this.repeatInterval_1 + ', remindEnabled=' + this.remindEnabled_1 + ', remindHour=' + this.remindHour_1 + ', remindMinute=' + this.remindMinute_1 + ', createdAt=' + this.createdAt_1.toString() + ', updatedAt=' + this.updatedAt_1.toString() + ')';
  };
  protoOf(DayEventDto).hashCode = function () {
    var result = getStringHashCode(this.id_1);
    result = imul(result, 31) + getStringHashCode(this.title_1) | 0;
    result = imul(result, 31) + getStringHashCode(this.date_1) | 0;
    result = imul(result, 31) + (this.note_1 == null ? 0 : getStringHashCode(this.note_1)) | 0;
    result = imul(result, 31) + getBooleanHashCode(this.starred_1) | 0;
    result = imul(result, 31) + (this.starRank_1 == null ? 0 : this.starRank_1) | 0;
    result = imul(result, 31) + getStringHashCode(this.repeatUnit_1) | 0;
    result = imul(result, 31) + this.repeatInterval_1 | 0;
    result = imul(result, 31) + getBooleanHashCode(this.remindEnabled_1) | 0;
    result = imul(result, 31) + this.remindHour_1 | 0;
    result = imul(result, 31) + this.remindMinute_1 | 0;
    result = imul(result, 31) + this.createdAt_1.hashCode() | 0;
    result = imul(result, 31) + this.updatedAt_1.hashCode() | 0;
    return result;
  };
  protoOf(DayEventDto).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof DayEventDto))
      return false;
    var tmp0_other_with_cast = other instanceof DayEventDto ? other : THROW_CCE();
    if (!(this.id_1 === tmp0_other_with_cast.id_1))
      return false;
    if (!(this.title_1 === tmp0_other_with_cast.title_1))
      return false;
    if (!(this.date_1 === tmp0_other_with_cast.date_1))
      return false;
    if (!(this.note_1 == tmp0_other_with_cast.note_1))
      return false;
    if (!(this.starred_1 === tmp0_other_with_cast.starred_1))
      return false;
    if (!(this.starRank_1 == tmp0_other_with_cast.starRank_1))
      return false;
    if (!(this.repeatUnit_1 === tmp0_other_with_cast.repeatUnit_1))
      return false;
    if (!(this.repeatInterval_1 === tmp0_other_with_cast.repeatInterval_1))
      return false;
    if (!(this.remindEnabled_1 === tmp0_other_with_cast.remindEnabled_1))
      return false;
    if (!(this.remindHour_1 === tmp0_other_with_cast.remindHour_1))
      return false;
    if (!(this.remindMinute_1 === tmp0_other_with_cast.remindMinute_1))
      return false;
    if (!this.createdAt_1.equals(tmp0_other_with_cast.createdAt_1))
      return false;
    if (!this.updatedAt_1.equals(tmp0_other_with_cast.updatedAt_1))
      return false;
    return true;
  };
  function toDto(_this__u8e3s4, $this) {
    var tmp = _this__u8e3s4.get_id_kntnx8_k$();
    var tmp_0 = _this__u8e3s4.get_title_iz32un_k$();
    var tmp_1 = _this__u8e3s4.get_date_wokkxj_k$();
    var tmp_2 = _this__u8e3s4.get_note_wor96j_k$();
    var tmp_3 = _this__u8e3s4.get_starred_u3zcve_k$();
    var tmp_4 = _this__u8e3s4.get_starRank_a1jvkp_k$();
    // Inline function 'kotlin.text.lowercase' call
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$1 = _this__u8e3s4.get_repeatUnit_zunjs_k$().get_name_woqyms_k$().toLowerCase();
    return new DayEventDto(tmp, tmp_0, tmp_1, tmp_2, tmp_3, tmp_4, tmp$ret$1, _this__u8e3s4.get_repeatInterval_awuft5_k$(), _this__u8e3s4.get_remindEnabled_8nmteb_k$(), _this__u8e3s4.get_remindHour_muku0e_k$(), _this__u8e3s4.get_remindMinute_7gj5fi_k$(), _this__u8e3s4.get_createdAt_ierzpu_k$(), _this__u8e3s4.get_updatedAt_npz717_k$());
  }
  function toDomainOrNull(_this__u8e3s4, $this) {
    var tmp;
    // Inline function 'kotlin.text.isEmpty' call
    var this_0 = _this__u8e3s4.id_1;
    if (charSequenceLength(this_0) === 0) {
      tmp = true;
    } else {
      // Inline function 'kotlin.text.isEmpty' call
      var this_1 = _this__u8e3s4.date_1;
      tmp = charSequenceLength(this_1) === 0;
    }
    if (tmp)
      return null;
    var tmp0_safe_receiver = _this__u8e3s4.note_1;
    var tmp_0;
    if (tmp0_safe_receiver == null) {
      tmp_0 = null;
    } else {
      // Inline function 'kotlin.text.ifEmpty' call
      // Inline function 'kotlin.contracts.contract' call
      var tmp_1;
      // Inline function 'kotlin.text.isEmpty' call
      if (charSequenceLength(tmp0_safe_receiver) === 0) {
        // Inline function 'com.numberourdays.app.data.DayEventCodec.toDomainOrNull.<anonymous>' call
        tmp_1 = null;
      } else {
        tmp_1 = tmp0_safe_receiver;
      }
      tmp_0 = tmp_1;
    }
    return new DayEvent(_this__u8e3s4.id_1, _this__u8e3s4.title_1, _this__u8e3s4.date_1, tmp_0, _this__u8e3s4.starred_1, _this__u8e3s4.starRank_1, Companion_getInstance_5().from_1jvler_k$(_this__u8e3s4.repeatUnit_1), coerceAtLeast(_this__u8e3s4.repeatInterval_1, 1), _this__u8e3s4.remindEnabled_1, coerceIn(_this__u8e3s4.remindHour_1, 0, 23), coerceIn(_this__u8e3s4.remindMinute_1, 0, 59), _this__u8e3s4.createdAt_1, _this__u8e3s4.updatedAt_1);
  }
  function merge$naturalKey(e) {
    // Inline function 'kotlin.text.trim' call
    var this_0 = e.get_title_iz32un_k$();
    var tmp$ret$0 = toString(trim(isCharSequence(this_0) ? this_0 : THROW_CCE()));
    return joinToString(listOf([tmp$ret$0, e.get_date_wokkxj_k$(), e.get_repeatUnit_zunjs_k$().get_name_woqyms_k$(), e.get_repeatInterval_awuft5_k$().toString()]), '|');
  }
  function DayEventCodec$json$lambda($this$Json) {
    $this$Json.set_ignoreUnknownKeys_pzvtne_k$(true);
    $this$Json.set_encodeDefaults_c5evsg_k$(true);
    $this$Json.set_explicitNulls_mtj2ai_k$(true);
    $this$Json.set_prettyPrint_v2gnff_k$(true);
    $this$Json.set_prettyPrintIndent_ab0258_k$('  ');
    return Unit_getInstance();
  }
  function DayEventCodec() {
    DayEventCodec_instance = this;
    var tmp = this;
    tmp.json_1 = Json(VOID, DayEventCodec$json$lambda);
  }
  protoOf(DayEventCodec).toJson_1ebj9p_k$ = function (e) {
    // Inline function 'kotlinx.serialization.encodeToString' call
    var this_0 = this.json_1;
    var value = toDto(e, this);
    // Inline function 'kotlinx.serialization.serializer' call
    var this_1 = this_0.get_serializersModule_piitvg_k$();
    // Inline function 'kotlinx.serialization.internal.cast' call
    var this_2 = serializer(this_1, createKType(getKClass(DayEventDto), arrayOf([]), false));
    var tmp$ret$1 = isInterface(this_2, KSerializer) ? this_2 : THROW_CCE();
    return this_0.encodeToString_k0apqx_k$(tmp$ret$1, value);
  };
  protoOf(DayEventCodec).migrate_w3le1o_k$ = function (text) {
    // Inline function 'kotlin.text.isNullOrBlank' call
    // Inline function 'kotlin.contracts.contract' call
    if (text == null || isBlank(text))
      return null;
    // Inline function 'kotlin.Result.getOrNull' call
    // Inline function 'kotlin.runCatching' call
    var tmp;
    try {
      // Inline function 'kotlin.Companion.success' call
      Companion_getInstance();
      // Inline function 'com.numberourdays.app.data.DayEventCodec.migrate.<anonymous>' call
      // Inline function 'kotlinx.serialization.json.Json.decodeFromString' call
      var this_0 = this.json_1;
      // Inline function 'kotlinx.serialization.serializer' call
      var this_1 = this_0.get_serializersModule_piitvg_k$();
      // Inline function 'kotlinx.serialization.internal.cast' call
      var this_2 = serializer(this_1, createKType(getKClass(DayEventDto), arrayOf([]), false));
      var tmp$ret$2 = isInterface(this_2, KSerializer) ? this_2 : THROW_CCE();
      var tmp$ret$3 = this_0.decodeFromString_jwu9sq_k$(tmp$ret$2, text);
      var value = toDomainOrNull(tmp$ret$3, this);
      tmp = _Result___init__impl__xyqfz8(value);
    } catch ($p) {
      var tmp_0;
      if ($p instanceof Error) {
        var e = $p;
        // Inline function 'kotlin.Companion.failure' call
        Companion_getInstance();
        tmp_0 = _Result___init__impl__xyqfz8(createFailure(e));
      } else {
        throw $p;
      }
      tmp = tmp_0;
    }
    var this_3 = tmp;
    var tmp_1;
    if (_Result___get_isFailure__impl__jpiriv(this_3)) {
      tmp_1 = null;
    } else {
      var tmp_2 = _Result___get_value__impl__bjfvqg(this_3);
      tmp_1 = (tmp_2 == null ? true : !(tmp_2 == null)) ? tmp_2 : THROW_CCE();
    }
    return tmp_1;
  };
  protoOf(DayEventCodec).migrate_4fi71g_k$ = function (obj) {
    if (obj == null)
      return null;
    // Inline function 'kotlin.Result.getOrNull' call
    // Inline function 'kotlin.runCatching' call
    var tmp;
    try {
      // Inline function 'kotlin.Companion.success' call
      Companion_getInstance();
      // Inline function 'com.numberourdays.app.data.DayEventCodec.migrate.<anonymous>' call
      var value = toDomainOrNull(this.json_1.decodeFromJsonElement_tsogwj_k$(Companion_getInstance_4().serializer_9w0wvi_k$(), obj), this);
      tmp = _Result___init__impl__xyqfz8(value);
    } catch ($p) {
      var tmp_0;
      if ($p instanceof Error) {
        var e = $p;
        // Inline function 'kotlin.Companion.failure' call
        Companion_getInstance();
        tmp_0 = _Result___init__impl__xyqfz8(createFailure(e));
      } else {
        throw $p;
      }
      tmp = tmp_0;
    }
    var this_0 = tmp;
    var tmp_1;
    if (_Result___get_isFailure__impl__jpiriv(this_0)) {
      tmp_1 = null;
    } else {
      var tmp_2 = _Result___get_value__impl__bjfvqg(this_0);
      tmp_1 = (tmp_2 == null ? true : !(tmp_2 == null)) ? tmp_2 : THROW_CCE();
    }
    return tmp_1;
  };
  protoOf(DayEventCodec).parseEventsArray_lu4k7h_k$ = function (raw) {
    // Inline function 'kotlin.getOrElse' call
    // Inline function 'kotlin.runCatching' call
    var tmp;
    try {
      // Inline function 'kotlin.Companion.success' call
      Companion_getInstance();
      // Inline function 'com.numberourdays.app.data.DayEventCodec.parseEventsArray.<anonymous>' call
      var arr = get_jsonArray(this.json_1.parseToJsonElement_rqvr2k_k$(raw));
      // Inline function 'kotlin.collections.mapNotNull' call
      // Inline function 'kotlin.collections.mapNotNullTo' call
      var destination = ArrayList_init_$Create$();
      // Inline function 'kotlin.collections.forEach' call
      var tmp0_iterator = arr.iterator_jk1svi_k$();
      while (tmp0_iterator.hasNext_bitz1p_k$()) {
        var element = tmp0_iterator.next_20eer_k$();
        // Inline function 'kotlin.collections.mapNotNullTo.<anonymous>' call
        // Inline function 'com.numberourdays.app.data.DayEventCodec.parseEventsArray.<anonymous>.<anonymous>' call
        var tmp0_safe_receiver = this.migrate_4fi71g_k$(element instanceof JsonObject ? element : null);
        if (tmp0_safe_receiver == null)
          null;
        else {
          // Inline function 'kotlin.let' call
          // Inline function 'kotlin.contracts.contract' call
          destination.add_utx5q5_k$(tmp0_safe_receiver);
        }
      }
      tmp = _Result___init__impl__xyqfz8(destination);
    } catch ($p) {
      var tmp_0;
      if ($p instanceof Error) {
        var e = $p;
        // Inline function 'kotlin.Companion.failure' call
        Companion_getInstance();
        tmp_0 = _Result___init__impl__xyqfz8(createFailure(e));
      } else {
        throw $p;
      }
      tmp = tmp_0;
    }
    var this_0 = tmp;
    // Inline function 'kotlin.contracts.contract' call
    var exception = Result__exceptionOrNull_impl_p6xea9(this_0);
    var tmp_1;
    if (exception == null) {
      var tmp_2 = _Result___get_value__impl__bjfvqg(this_0);
      tmp_1 = (tmp_2 == null ? true : !(tmp_2 == null)) ? tmp_2 : THROW_CCE();
    } else {
      // Inline function 'com.numberourdays.app.data.DayEventCodec.parseEventsArray.<anonymous>' call
      tmp_1 = emptyList();
    }
    return tmp_1;
  };
  protoOf(DayEventCodec).serialize_7sr98y_k$ = function (events) {
    // Inline function 'kotlinx.serialization.json.buildJsonObject' call
    var builder = new JsonObjectBuilder();
    // Inline function 'com.numberourdays.app.data.DayEventCodec.serialize.<anonymous>' call
    put(builder, 'app', 'NumberOurDays');
    put_0(builder, 'schemaVersion', 1);
    put_0(builder, 'exportedAt', System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$());
    // Inline function 'kotlinx.serialization.json.buildJsonArray' call
    var builder_0 = new JsonArrayBuilder();
    // Inline function 'com.numberourdays.app.data.DayEventCodec.serialize.<anonymous>.<anonymous>' call
    // Inline function 'kotlin.collections.forEach' call
    var tmp0_iterator = events.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.data.DayEventCodec.serialize.<anonymous>.<anonymous>.<anonymous>' call
      var tmp = DayEventCodec_getInstance().json_1;
      // Inline function 'kotlinx.serialization.encodeToString' call
      var this_0 = DayEventCodec_getInstance().json_1;
      var value = toDto(element, DayEventCodec_getInstance());
      // Inline function 'kotlinx.serialization.serializer' call
      var this_1 = this_0.get_serializersModule_piitvg_k$();
      // Inline function 'kotlinx.serialization.internal.cast' call
      var this_2 = serializer(this_1, createKType(getKClass(DayEventDto), arrayOf([]), false));
      var tmp$ret$1 = isInterface(this_2, KSerializer) ? this_2 : THROW_CCE();
      var tmp$ret$2 = this_0.encodeToString_k0apqx_k$(tmp$ret$1, value);
      builder_0.add_i0x14v_k$(tmp.parseToJsonElement_rqvr2k_k$(tmp$ret$2));
    }
    var tmp$ret$3 = builder_0.build_bkh6qh_k$();
    builder.put_mrde5n_k$('events', tmp$ret$3);
    var payload = builder.build_bkh6qh_k$();
    return this.json_1.encodeToString_k0apqx_k$(Companion_getInstance_0().serializer_9w0wvi_k$(), payload);
  };
  protoOf(DayEventCodec).parseBackup_xtnnyd_k$ = function (text) {
    // Inline function 'kotlin.text.trim' call
    var trimmed = toString(trim(isCharSequence(text) ? text : THROW_CCE()));
    // Inline function 'kotlin.text.isEmpty' call
    if (charSequenceLength(trimmed) === 0)
      return to(emptyList(), '\u5185\u5BB9\u4E3A\u7A7A');
    var tmp;
    try {
      var data = get_jsonObject(this.json_1.parseToJsonElement_rqvr2k_k$(trimmed));
      var tmp0_safe_receiver = data.get_6bo4tg_k$('events');
      var tmp1_elvis_lhs = tmp0_safe_receiver == null ? null : get_jsonArray(tmp0_safe_receiver);
      var tmp_0;
      if (tmp1_elvis_lhs == null) {
        return to(emptyList(), '\u7F3A\u5C11 events \u6570\u7EC4');
      } else {
        tmp_0 = tmp1_elvis_lhs;
      }
      var arr = tmp_0;
      // Inline function 'kotlin.collections.mutableListOf' call
      var events = ArrayList_init_$Create$();
      var skipped = 0;
      var _iterator__ex2g4s = arr.iterator_jk1svi_k$();
      while (_iterator__ex2g4s.hasNext_bitz1p_k$()) {
        var element = _iterator__ex2g4s.next_20eer_k$();
        var e = this.migrate_4fi71g_k$(element instanceof JsonObject ? element : null);
        var tmp_1;
        var tmp_2;
        if (!(e == null)) {
          // Inline function 'kotlin.text.isNotBlank' call
          var this_0 = e.get_title_iz32un_k$();
          tmp_2 = !isBlank(this_0);
        } else {
          tmp_2 = false;
        }
        if (tmp_2) {
          tmp_1 = DateCalculator_getInstance().isValidDate_5zjlfd_k$(e.get_date_wokkxj_k$());
        } else {
          tmp_1 = false;
        }
        if (tmp_1)
          events.add_utx5q5_k$(e);
        else {
          skipped = skipped + 1 | 0;
        }
      }
      var note = skipped > 0 ? '\u8DF3\u8FC7 ' + skipped + ' \u6761\u65E0\u6548' : null;
      tmp = to(events, note);
    } catch ($p) {
      var tmp_3;
      if ($p instanceof Exception) {
        var _unused_var__etf5q3 = $p;
        tmp_3 = to(emptyList(), '\u4E0D\u662F\u5408\u6CD5 JSON');
      } else {
        throw $p;
      }
      tmp = tmp_3;
    }
    return tmp;
  };
  protoOf(DayEventCodec).merge_ri0zj8_k$ = function (local, incoming) {
    var map = LinkedHashMap_init_$Create$();
    var keyToId = HashMap_init_$Create$();
    // Inline function 'kotlin.collections.forEach' call
    var tmp0_iterator = local.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.data.DayEventCodec.merge.<anonymous>' call
      // Inline function 'kotlin.collections.set' call
      var key = element.get_id_kntnx8_k$();
      map.put_4fpzoq_k$(key, element);
      var key_0 = merge$naturalKey(element);
      if (!keyToId.containsKey_aw81wo_k$(key_0)) {
        // Inline function 'kotlin.collections.set' call
        var value = element.get_id_kntnx8_k$();
        keyToId.put_4fpzoq_k$(key_0, value);
      }
    }
    // Inline function 'kotlin.collections.forEach' call
    var tmp0_iterator_0 = incoming.iterator_jk1svi_k$();
    while (tmp0_iterator_0.hasNext_bitz1p_k$()) {
      var element_0 = tmp0_iterator_0.next_20eer_k$();
      // Inline function 'com.numberourdays.app.data.DayEventCodec.merge.<anonymous>' call
      var tmp0_elvis_lhs = map.get_wei43m_k$(element_0.get_id_kntnx8_k$());
      var tmp;
      if (tmp0_elvis_lhs == null) {
        var tmp1_safe_receiver = keyToId.get_wei43m_k$(merge$naturalKey(element_0));
        var tmp_0;
        if (tmp1_safe_receiver == null) {
          tmp_0 = null;
        } else {
          // Inline function 'kotlin.let' call
          // Inline function 'kotlin.contracts.contract' call
          // Inline function 'com.numberourdays.app.data.DayEventCodec.merge.<anonymous>.<anonymous>' call
          tmp_0 = map.get_wei43m_k$(tmp1_safe_receiver);
        }
        tmp = tmp_0;
      } else {
        tmp = tmp0_elvis_lhs;
      }
      var l = tmp;
      if (l == null) {
        // Inline function 'kotlin.collections.set' call
        var key_1 = element_0.get_id_kntnx8_k$();
        map.put_4fpzoq_k$(key_1, element_0);
        // Inline function 'kotlin.collections.set' call
        var key_2 = merge$naturalKey(element_0);
        var value_0 = element_0.get_id_kntnx8_k$();
        keyToId.put_4fpzoq_k$(key_2, value_0);
      } else if (element_0.get_updatedAt_npz717_k$().compareTo_9jj042_k$(l.get_updatedAt_npz717_k$()) >= 0) {
        var updated = element_0.copy$default_9a50ga_k$(l.get_id_kntnx8_k$());
        // Inline function 'kotlin.collections.set' call
        var key_3 = l.get_id_kntnx8_k$();
        map.put_4fpzoq_k$(key_3, updated);
        keyToId.remove_gppy8k_k$(merge$naturalKey(l));
        // Inline function 'kotlin.collections.set' call
        var key_4 = merge$naturalKey(updated);
        var value_1 = l.get_id_kntnx8_k$();
        keyToId.put_4fpzoq_k$(key_4, value_1);
      } else {
        // Inline function 'kotlin.collections.set' call
        var key_5 = merge$naturalKey(l);
        var value_2 = l.get_id_kntnx8_k$();
        keyToId.put_4fpzoq_k$(key_5, value_2);
      }
    }
    return toList(map.get_values_ksazhn_k$());
  };
  var DayEventCodec_instance;
  function DayEventCodec_getInstance() {
    if (DayEventCodec_instance == null)
      new DayEventCodec();
    return DayEventCodec_instance;
  }
  function createEventId(now) {
    now = now === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : now;
    var ts = toString_0(now, 36);
    var rand = toString_0(Default_getInstance().nextLong_m0lbld_k$(new Long(0, 0), new Long(0, 256)), 36);
    return 'e_' + ts + '_' + rand;
  }
  function sameWeekSaturday($this, from) {
    var offset = DayOfWeek_SATURDAY_getInstance().get_ordinal_ip24qg_k$() - from.get_dayOfWeek_3kfgci_k$().get_ordinal_ip24qg_k$() | 0;
    return plus(from, DatePeriod_init_$Create$(VOID, VOID, offset));
  }
  function DemoEvents() {
    DemoEvents_instance = this;
  }
  protoOf(DemoEvents).create_frgqku_k$ = function (now) {
    var today = todayIn(System_getInstance(), Companion_getInstance_1().currentSystemDefault_9rduv_k$());
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.numberourdays.app.data.DemoEvents.create.<anonymous>' call
    var it = sameWeekSaturday(this, today);
    var sat = it.compareTo_phv9sr_k$(today) < 0 ? plus(it, DatePeriod_init_$Create$(VOID, VOID, 7)) : it;
    // Inline function 'kotlin.let' call
    // Inline function 'kotlin.contracts.contract' call
    // Inline function 'com.numberourdays.app.data.DemoEvents.create.<anonymous>' call
    var it_0 = LocalDate_init_$Create$(today.get_monthNumber_nb47ls_k$() >= 12 ? today.get_year_woy26e_k$() + 1 | 0 : today.get_year_woy26e_k$(), 1, 1);
    var newYear = it_0.compareTo_phv9sr_k$(today) < 0 ? plus(it_0, DatePeriod_init_$Create$(1)) : it_0;
    return listOf([new DayEvent('demo_weekend', '\u5468\u672B', DateCalculator_getInstance().format_36xaz2_k$(sat), '\u6BCF\u5468\u91CD\u590D\u5012\u6570\u6F14\u793A', true, 1, RepeatUnit_WEEK_getInstance(), 1, VOID, VOID, VOID, now, now), new DayEvent('demo_newyear', '\u65B0\u5E74', DateCalculator_getInstance().format_36xaz2_k$(newYear), '\u672A\u6765\u5012\u6570\u6F14\u793A', true, 2, VOID, VOID, VOID, VOID, VOID, now, now)]);
  };
  protoOf(DemoEvents).create$default_mwqfzz_k$ = function (now, $super) {
    now = now === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : now;
    return $super === VOID ? this.create_frgqku_k$(now) : $super.create_frgqku_k$.call(this, now);
  };
  var DemoEvents_instance;
  function DemoEvents_getInstance() {
    if (DemoEvents_instance == null)
      new DemoEvents();
    return DemoEvents_instance;
  }
  function EventRepository() {
  }
  function HolidayPeriod(name, start, end) {
    this.name_1 = name;
    this.start_1 = start;
    this.end_1 = end;
  }
  protoOf(HolidayPeriod).get_name_woqyms_k$ = function () {
    return this.name_1;
  };
  protoOf(HolidayPeriod).get_start_iypx6h_k$ = function () {
    return this.start_1;
  };
  protoOf(HolidayPeriod).get_end_18j6ha_k$ = function () {
    return this.end_1;
  };
  protoOf(HolidayPeriod).component1_7eebsc_k$ = function () {
    return this.name_1;
  };
  protoOf(HolidayPeriod).component2_7eebsb_k$ = function () {
    return this.start_1;
  };
  protoOf(HolidayPeriod).component3_7eebsa_k$ = function () {
    return this.end_1;
  };
  protoOf(HolidayPeriod).copy_pgpwhn_k$ = function (name, start, end) {
    return new HolidayPeriod(name, start, end);
  };
  protoOf(HolidayPeriod).copy$default_z1iyd9_k$ = function (name, start, end, $super) {
    name = name === VOID ? this.name_1 : name;
    start = start === VOID ? this.start_1 : start;
    end = end === VOID ? this.end_1 : end;
    return $super === VOID ? this.copy_pgpwhn_k$(name, start, end) : $super.copy_pgpwhn_k$.call(this, name, start, end);
  };
  protoOf(HolidayPeriod).toString = function () {
    return 'HolidayPeriod(name=' + this.name_1 + ', start=' + this.start_1.toString() + ', end=' + this.end_1.toString() + ')';
  };
  protoOf(HolidayPeriod).hashCode = function () {
    var result = getStringHashCode(this.name_1);
    result = imul(result, 31) + this.start_1.hashCode() | 0;
    result = imul(result, 31) + this.end_1.hashCode() | 0;
    return result;
  };
  protoOf(HolidayPeriod).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof HolidayPeriod))
      return false;
    var tmp0_other_with_cast = other instanceof HolidayPeriod ? other : THROW_CCE();
    if (!(this.name_1 === tmp0_other_with_cast.name_1))
      return false;
    if (!this.start_1.equals(tmp0_other_with_cast.start_1))
      return false;
    if (!this.end_1.equals(tmp0_other_with_cast.end_1))
      return false;
    return true;
  };
  var HolidayStatus_UPCOMING_instance;
  var HolidayStatus_ONGOING_instance;
  function values() {
    return [HolidayStatus_UPCOMING_getInstance(), HolidayStatus_ONGOING_getInstance()];
  }
  function valueOf(value) {
    switch (value) {
      case 'UPCOMING':
        return HolidayStatus_UPCOMING_getInstance();
      case 'ONGOING':
        return HolidayStatus_ONGOING_getInstance();
      default:
        HolidayStatus_initEntries();
        THROW_IAE('No enum constant value.');
        break;
    }
  }
  function get_entries() {
    if ($ENTRIES == null)
      $ENTRIES = enumEntries(values());
    return $ENTRIES;
  }
  var HolidayStatus_entriesInitialized;
  function HolidayStatus_initEntries() {
    if (HolidayStatus_entriesInitialized)
      return Unit_getInstance();
    HolidayStatus_entriesInitialized = true;
    HolidayStatus_UPCOMING_instance = new HolidayStatus('UPCOMING', 0);
    HolidayStatus_ONGOING_instance = new HolidayStatus('ONGOING', 1);
  }
  var $ENTRIES;
  function HolidayStatus(name, ordinal) {
    Enum.call(this, name, ordinal);
  }
  function HolidayCountdown(period, status, daysUntilStart, label) {
    this.period_1 = period;
    this.status_1 = status;
    this.daysUntilStart_1 = daysUntilStart;
    this.label_1 = label;
  }
  protoOf(HolidayCountdown).get_period_i0c0qy_k$ = function () {
    return this.period_1;
  };
  protoOf(HolidayCountdown).get_status_jnf6d7_k$ = function () {
    return this.status_1;
  };
  protoOf(HolidayCountdown).get_daysUntilStart_5obrmk_k$ = function () {
    return this.daysUntilStart_1;
  };
  protoOf(HolidayCountdown).get_label_iuj8p7_k$ = function () {
    return this.label_1;
  };
  protoOf(HolidayCountdown).component1_7eebsc_k$ = function () {
    return this.period_1;
  };
  protoOf(HolidayCountdown).component2_7eebsb_k$ = function () {
    return this.status_1;
  };
  protoOf(HolidayCountdown).component3_7eebsa_k$ = function () {
    return this.daysUntilStart_1;
  };
  protoOf(HolidayCountdown).component4_7eebs9_k$ = function () {
    return this.label_1;
  };
  protoOf(HolidayCountdown).copy_hg9die_k$ = function (period, status, daysUntilStart, label) {
    return new HolidayCountdown(period, status, daysUntilStart, label);
  };
  protoOf(HolidayCountdown).copy$default_masttt_k$ = function (period, status, daysUntilStart, label, $super) {
    period = period === VOID ? this.period_1 : period;
    status = status === VOID ? this.status_1 : status;
    daysUntilStart = daysUntilStart === VOID ? this.daysUntilStart_1 : daysUntilStart;
    label = label === VOID ? this.label_1 : label;
    return $super === VOID ? this.copy_hg9die_k$(period, status, daysUntilStart, label) : $super.copy_hg9die_k$.call(this, period, status, daysUntilStart, label);
  };
  protoOf(HolidayCountdown).toString = function () {
    return 'HolidayCountdown(period=' + this.period_1.toString() + ', status=' + this.status_1.toString() + ', daysUntilStart=' + this.daysUntilStart_1 + ', label=' + this.label_1 + ')';
  };
  protoOf(HolidayCountdown).hashCode = function () {
    var result = this.period_1.hashCode();
    result = imul(result, 31) + this.status_1.hashCode() | 0;
    result = imul(result, 31) + this.daysUntilStart_1 | 0;
    result = imul(result, 31) + getStringHashCode(this.label_1) | 0;
    return result;
  };
  protoOf(HolidayCountdown).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof HolidayCountdown))
      return false;
    var tmp0_other_with_cast = other instanceof HolidayCountdown ? other : THROW_CCE();
    if (!this.period_1.equals(tmp0_other_with_cast.period_1))
      return false;
    if (!this.status_1.equals(tmp0_other_with_cast.status_1))
      return false;
    if (!(this.daysUntilStart_1 === tmp0_other_with_cast.daysUntilStart_1))
      return false;
    if (!(this.label_1 === tmp0_other_with_cast.label_1))
      return false;
    return true;
  };
  function _get_Y2026__olcmga($this) {
    return $this.Y2026__1;
  }
  function LegalHolidays() {
    LegalHolidays_instance = this;
    this.Y2026__1 = listOf([new HolidayPeriod('\u5143\u65E6', LocalDate_init_$Create$(2026, 1, 1), LocalDate_init_$Create$(2026, 1, 3)), new HolidayPeriod('\u6625\u8282', LocalDate_init_$Create$(2026, 2, 15), LocalDate_init_$Create$(2026, 2, 23)), new HolidayPeriod('\u6E05\u660E\u8282', LocalDate_init_$Create$(2026, 4, 4), LocalDate_init_$Create$(2026, 4, 6)), new HolidayPeriod('\u52B3\u52A8\u8282', LocalDate_init_$Create$(2026, 5, 1), LocalDate_init_$Create$(2026, 5, 5)), new HolidayPeriod('\u7AEF\u5348\u8282', LocalDate_init_$Create$(2026, 6, 19), LocalDate_init_$Create$(2026, 6, 21)), new HolidayPeriod('\u4E2D\u79CB\u8282', LocalDate_init_$Create$(2026, 9, 25), LocalDate_init_$Create$(2026, 9, 27)), new HolidayPeriod('\u56FD\u5E86\u8282', LocalDate_init_$Create$(2026, 10, 1), LocalDate_init_$Create$(2026, 10, 7))]);
  }
  protoOf(LegalHolidays).periods_b83qjy_k$ = function () {
    return this.Y2026__1;
  };
  var LegalHolidays_instance;
  function LegalHolidays_getInstance() {
    if (LegalHolidays_instance == null)
      new LegalHolidays();
    return LegalHolidays_instance;
  }
  function HolidayStatus_UPCOMING_getInstance() {
    HolidayStatus_initEntries();
    return HolidayStatus_UPCOMING_instance;
  }
  function HolidayStatus_ONGOING_getInstance() {
    HolidayStatus_initEntries();
    return HolidayStatus_ONGOING_instance;
  }
  var RepeatUnit_NONE_instance;
  var RepeatUnit_DAY_instance;
  var RepeatUnit_WEEK_instance;
  var RepeatUnit_MONTH_instance;
  var RepeatUnit_YEAR_instance;
  function Companion_0() {
    Companion_instance_0 = this;
  }
  protoOf(Companion_0).from_1jvler_k$ = function (raw) {
    var tmp;
    if (raw == null) {
      tmp = null;
    } else {
      // Inline function 'kotlin.text.lowercase' call
      // Inline function 'kotlin.js.asDynamic' call
      tmp = raw.toLowerCase();
    }
    switch (tmp) {
      case 'day':
        return RepeatUnit_DAY_getInstance();
      case 'week':
        return RepeatUnit_WEEK_getInstance();
      case 'month':
        return RepeatUnit_MONTH_getInstance();
      case 'year':
        return RepeatUnit_YEAR_getInstance();
      default:
        return RepeatUnit_NONE_getInstance();
    }
  };
  var Companion_instance_0;
  function Companion_getInstance_5() {
    RepeatUnit_initEntries();
    if (Companion_instance_0 == null)
      new Companion_0();
    return Companion_instance_0;
  }
  function values_0() {
    return [RepeatUnit_NONE_getInstance(), RepeatUnit_DAY_getInstance(), RepeatUnit_WEEK_getInstance(), RepeatUnit_MONTH_getInstance(), RepeatUnit_YEAR_getInstance()];
  }
  function valueOf_0(value) {
    switch (value) {
      case 'NONE':
        return RepeatUnit_NONE_getInstance();
      case 'DAY':
        return RepeatUnit_DAY_getInstance();
      case 'WEEK':
        return RepeatUnit_WEEK_getInstance();
      case 'MONTH':
        return RepeatUnit_MONTH_getInstance();
      case 'YEAR':
        return RepeatUnit_YEAR_getInstance();
      default:
        RepeatUnit_initEntries();
        THROW_IAE('No enum constant value.');
        break;
    }
  }
  function get_entries_0() {
    if ($ENTRIES_0 == null)
      $ENTRIES_0 = enumEntries(values_0());
    return $ENTRIES_0;
  }
  var RepeatUnit_entriesInitialized;
  function RepeatUnit_initEntries() {
    if (RepeatUnit_entriesInitialized)
      return Unit_getInstance();
    RepeatUnit_entriesInitialized = true;
    RepeatUnit_NONE_instance = new RepeatUnit('NONE', 0);
    RepeatUnit_DAY_instance = new RepeatUnit('DAY', 1);
    RepeatUnit_WEEK_instance = new RepeatUnit('WEEK', 2);
    RepeatUnit_MONTH_instance = new RepeatUnit('MONTH', 3);
    RepeatUnit_YEAR_instance = new RepeatUnit('YEAR', 4);
    Companion_getInstance_5();
  }
  var $ENTRIES_0;
  function RepeatUnit(name, ordinal) {
    Enum.call(this, name, ordinal);
  }
  var EventKind_COUNTDOWN_instance;
  var EventKind_ELAPSED_instance;
  var EventKind_TODAY_instance;
  var EventKind_REPEAT_instance;
  function values_1() {
    return [EventKind_COUNTDOWN_getInstance(), EventKind_ELAPSED_getInstance(), EventKind_TODAY_getInstance(), EventKind_REPEAT_getInstance()];
  }
  function valueOf_1(value) {
    switch (value) {
      case 'COUNTDOWN':
        return EventKind_COUNTDOWN_getInstance();
      case 'ELAPSED':
        return EventKind_ELAPSED_getInstance();
      case 'TODAY':
        return EventKind_TODAY_getInstance();
      case 'REPEAT':
        return EventKind_REPEAT_getInstance();
      default:
        EventKind_initEntries();
        THROW_IAE('No enum constant value.');
        break;
    }
  }
  function get_entries_1() {
    if ($ENTRIES_1 == null)
      $ENTRIES_1 = enumEntries(values_1());
    return $ENTRIES_1;
  }
  var EventKind_entriesInitialized;
  function EventKind_initEntries() {
    if (EventKind_entriesInitialized)
      return Unit_getInstance();
    EventKind_entriesInitialized = true;
    EventKind_COUNTDOWN_instance = new EventKind('COUNTDOWN', 0);
    EventKind_ELAPSED_instance = new EventKind('ELAPSED', 1);
    EventKind_TODAY_instance = new EventKind('TODAY', 2);
    EventKind_REPEAT_instance = new EventKind('REPEAT', 3);
  }
  var $ENTRIES_1;
  function EventKind(name, ordinal) {
    Enum.call(this, name, ordinal);
  }
  function DayEvent(id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt) {
    note = note === VOID ? null : note;
    starred = starred === VOID ? false : starred;
    starRank = starRank === VOID ? null : starRank;
    repeatUnit = repeatUnit === VOID ? RepeatUnit_NONE_getInstance() : repeatUnit;
    repeatInterval = repeatInterval === VOID ? 1 : repeatInterval;
    remindEnabled = remindEnabled === VOID ? false : remindEnabled;
    remindHour = remindHour === VOID ? 8 : remindHour;
    remindMinute = remindMinute === VOID ? 0 : remindMinute;
    createdAt = createdAt === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : createdAt;
    updatedAt = updatedAt === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : updatedAt;
    this.id_1 = id;
    this.title_1 = title;
    this.date_1 = date;
    this.note_1 = note;
    this.starred_1 = starred;
    this.starRank_1 = starRank;
    this.repeatUnit_1 = repeatUnit;
    this.repeatInterval_1 = repeatInterval;
    this.remindEnabled_1 = remindEnabled;
    this.remindHour_1 = remindHour;
    this.remindMinute_1 = remindMinute;
    this.createdAt_1 = createdAt;
    this.updatedAt_1 = updatedAt;
  }
  protoOf(DayEvent).get_id_kntnx8_k$ = function () {
    return this.id_1;
  };
  protoOf(DayEvent).get_title_iz32un_k$ = function () {
    return this.title_1;
  };
  protoOf(DayEvent).get_date_wokkxj_k$ = function () {
    return this.date_1;
  };
  protoOf(DayEvent).get_note_wor96j_k$ = function () {
    return this.note_1;
  };
  protoOf(DayEvent).get_starred_u3zcve_k$ = function () {
    return this.starred_1;
  };
  protoOf(DayEvent).get_starRank_a1jvkp_k$ = function () {
    return this.starRank_1;
  };
  protoOf(DayEvent).get_repeatUnit_zunjs_k$ = function () {
    return this.repeatUnit_1;
  };
  protoOf(DayEvent).get_repeatInterval_awuft5_k$ = function () {
    return this.repeatInterval_1;
  };
  protoOf(DayEvent).get_remindEnabled_8nmteb_k$ = function () {
    return this.remindEnabled_1;
  };
  protoOf(DayEvent).get_remindHour_muku0e_k$ = function () {
    return this.remindHour_1;
  };
  protoOf(DayEvent).get_remindMinute_7gj5fi_k$ = function () {
    return this.remindMinute_1;
  };
  protoOf(DayEvent).get_createdAt_ierzpu_k$ = function () {
    return this.createdAt_1;
  };
  protoOf(DayEvent).get_updatedAt_npz717_k$ = function () {
    return this.updatedAt_1;
  };
  protoOf(DayEvent).component1_7eebsc_k$ = function () {
    return this.id_1;
  };
  protoOf(DayEvent).component2_7eebsb_k$ = function () {
    return this.title_1;
  };
  protoOf(DayEvent).component3_7eebsa_k$ = function () {
    return this.date_1;
  };
  protoOf(DayEvent).component4_7eebs9_k$ = function () {
    return this.note_1;
  };
  protoOf(DayEvent).component5_7eebs8_k$ = function () {
    return this.starred_1;
  };
  protoOf(DayEvent).component6_7eebs7_k$ = function () {
    return this.starRank_1;
  };
  protoOf(DayEvent).component7_7eebs6_k$ = function () {
    return this.repeatUnit_1;
  };
  protoOf(DayEvent).component8_7eebs5_k$ = function () {
    return this.repeatInterval_1;
  };
  protoOf(DayEvent).component9_7eebs4_k$ = function () {
    return this.remindEnabled_1;
  };
  protoOf(DayEvent).component10_gazzfo_k$ = function () {
    return this.remindHour_1;
  };
  protoOf(DayEvent).component11_gazzfn_k$ = function () {
    return this.remindMinute_1;
  };
  protoOf(DayEvent).component12_gazzfm_k$ = function () {
    return this.createdAt_1;
  };
  protoOf(DayEvent).component13_gazzfl_k$ = function () {
    return this.updatedAt_1;
  };
  protoOf(DayEvent).copy_i6xfx5_k$ = function (id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt) {
    return new DayEvent(id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt);
  };
  protoOf(DayEvent).copy$default_9a50ga_k$ = function (id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt, $super) {
    id = id === VOID ? this.id_1 : id;
    title = title === VOID ? this.title_1 : title;
    date = date === VOID ? this.date_1 : date;
    note = note === VOID ? this.note_1 : note;
    starred = starred === VOID ? this.starred_1 : starred;
    starRank = starRank === VOID ? this.starRank_1 : starRank;
    repeatUnit = repeatUnit === VOID ? this.repeatUnit_1 : repeatUnit;
    repeatInterval = repeatInterval === VOID ? this.repeatInterval_1 : repeatInterval;
    remindEnabled = remindEnabled === VOID ? this.remindEnabled_1 : remindEnabled;
    remindHour = remindHour === VOID ? this.remindHour_1 : remindHour;
    remindMinute = remindMinute === VOID ? this.remindMinute_1 : remindMinute;
    createdAt = createdAt === VOID ? this.createdAt_1 : createdAt;
    updatedAt = updatedAt === VOID ? this.updatedAt_1 : updatedAt;
    return $super === VOID ? this.copy_i6xfx5_k$(id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt) : $super.copy_i6xfx5_k$.call(this, id, title, date, note, starred, starRank, repeatUnit, repeatInterval, remindEnabled, remindHour, remindMinute, createdAt, updatedAt);
  };
  protoOf(DayEvent).toString = function () {
    return 'DayEvent(id=' + this.id_1 + ', title=' + this.title_1 + ', date=' + this.date_1 + ', note=' + this.note_1 + ', starred=' + this.starred_1 + ', starRank=' + this.starRank_1 + ', repeatUnit=' + this.repeatUnit_1.toString() + ', repeatInterval=' + this.repeatInterval_1 + ', remindEnabled=' + this.remindEnabled_1 + ', remindHour=' + this.remindHour_1 + ', remindMinute=' + this.remindMinute_1 + ', createdAt=' + this.createdAt_1.toString() + ', updatedAt=' + this.updatedAt_1.toString() + ')';
  };
  protoOf(DayEvent).hashCode = function () {
    var result = getStringHashCode(this.id_1);
    result = imul(result, 31) + getStringHashCode(this.title_1) | 0;
    result = imul(result, 31) + getStringHashCode(this.date_1) | 0;
    result = imul(result, 31) + (this.note_1 == null ? 0 : getStringHashCode(this.note_1)) | 0;
    result = imul(result, 31) + getBooleanHashCode(this.starred_1) | 0;
    result = imul(result, 31) + (this.starRank_1 == null ? 0 : this.starRank_1) | 0;
    result = imul(result, 31) + this.repeatUnit_1.hashCode() | 0;
    result = imul(result, 31) + this.repeatInterval_1 | 0;
    result = imul(result, 31) + getBooleanHashCode(this.remindEnabled_1) | 0;
    result = imul(result, 31) + this.remindHour_1 | 0;
    result = imul(result, 31) + this.remindMinute_1 | 0;
    result = imul(result, 31) + this.createdAt_1.hashCode() | 0;
    result = imul(result, 31) + this.updatedAt_1.hashCode() | 0;
    return result;
  };
  protoOf(DayEvent).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof DayEvent))
      return false;
    var tmp0_other_with_cast = other instanceof DayEvent ? other : THROW_CCE();
    if (!(this.id_1 === tmp0_other_with_cast.id_1))
      return false;
    if (!(this.title_1 === tmp0_other_with_cast.title_1))
      return false;
    if (!(this.date_1 === tmp0_other_with_cast.date_1))
      return false;
    if (!(this.note_1 == tmp0_other_with_cast.note_1))
      return false;
    if (!(this.starred_1 === tmp0_other_with_cast.starred_1))
      return false;
    if (!(this.starRank_1 == tmp0_other_with_cast.starRank_1))
      return false;
    if (!this.repeatUnit_1.equals(tmp0_other_with_cast.repeatUnit_1))
      return false;
    if (!(this.repeatInterval_1 === tmp0_other_with_cast.repeatInterval_1))
      return false;
    if (!(this.remindEnabled_1 === tmp0_other_with_cast.remindEnabled_1))
      return false;
    if (!(this.remindHour_1 === tmp0_other_with_cast.remindHour_1))
      return false;
    if (!(this.remindMinute_1 === tmp0_other_with_cast.remindMinute_1))
      return false;
    if (!this.createdAt_1.equals(tmp0_other_with_cast.createdAt_1))
      return false;
    if (!this.updatedAt_1.equals(tmp0_other_with_cast.updatedAt_1))
      return false;
    return true;
  };
  function DayEventDisplay(event, kind, days, elapsedDays, label, nextDate, repeatLabel, sortDate) {
    nextDate = nextDate === VOID ? null : nextDate;
    repeatLabel = repeatLabel === VOID ? null : repeatLabel;
    this.event_1 = event;
    this.kind_1 = kind;
    this.days_1 = days;
    this.elapsedDays_1 = elapsedDays;
    this.label_1 = label;
    this.nextDate_1 = nextDate;
    this.repeatLabel_1 = repeatLabel;
    this.sortDate_1 = sortDate;
  }
  protoOf(DayEventDisplay).get_event_ir25pt_k$ = function () {
    return this.event_1;
  };
  protoOf(DayEventDisplay).get_kind_wop7ml_k$ = function () {
    return this.kind_1;
  };
  protoOf(DayEventDisplay).get_days_wokl28_k$ = function () {
    return this.days_1;
  };
  protoOf(DayEventDisplay).get_elapsedDays_5v4xai_k$ = function () {
    return this.elapsedDays_1;
  };
  protoOf(DayEventDisplay).get_label_iuj8p7_k$ = function () {
    return this.label_1;
  };
  protoOf(DayEventDisplay).get_nextDate_8969rq_k$ = function () {
    return this.nextDate_1;
  };
  protoOf(DayEventDisplay).get_repeatLabel_v0kvo0_k$ = function () {
    return this.repeatLabel_1;
  };
  protoOf(DayEventDisplay).get_sortDate_4bt0i3_k$ = function () {
    return this.sortDate_1;
  };
  protoOf(DayEventDisplay).get_id_kntnx8_k$ = function () {
    return this.event_1.id_1;
  };
  protoOf(DayEventDisplay).get_title_iz32un_k$ = function () {
    return this.event_1.title_1;
  };
  protoOf(DayEventDisplay).get_date_wokkxj_k$ = function () {
    return this.event_1.date_1;
  };
  protoOf(DayEventDisplay).get_starred_u3zcve_k$ = function () {
    return this.event_1.starred_1;
  };
  protoOf(DayEventDisplay).get_note_wor96j_k$ = function () {
    return this.event_1.note_1;
  };
  protoOf(DayEventDisplay).get_remindEnabled_8nmteb_k$ = function () {
    return this.event_1.remindEnabled_1;
  };
  protoOf(DayEventDisplay).get_remindHour_muku0e_k$ = function () {
    return this.event_1.remindHour_1;
  };
  protoOf(DayEventDisplay).get_remindMinute_7gj5fi_k$ = function () {
    return this.event_1.remindMinute_1;
  };
  protoOf(DayEventDisplay).component1_7eebsc_k$ = function () {
    return this.event_1;
  };
  protoOf(DayEventDisplay).component2_7eebsb_k$ = function () {
    return this.kind_1;
  };
  protoOf(DayEventDisplay).component3_7eebsa_k$ = function () {
    return this.days_1;
  };
  protoOf(DayEventDisplay).component4_7eebs9_k$ = function () {
    return this.elapsedDays_1;
  };
  protoOf(DayEventDisplay).component5_7eebs8_k$ = function () {
    return this.label_1;
  };
  protoOf(DayEventDisplay).component6_7eebs7_k$ = function () {
    return this.nextDate_1;
  };
  protoOf(DayEventDisplay).component7_7eebs6_k$ = function () {
    return this.repeatLabel_1;
  };
  protoOf(DayEventDisplay).component8_7eebs5_k$ = function () {
    return this.sortDate_1;
  };
  protoOf(DayEventDisplay).copy_4gdri7_k$ = function (event, kind, days, elapsedDays, label, nextDate, repeatLabel, sortDate) {
    return new DayEventDisplay(event, kind, days, elapsedDays, label, nextDate, repeatLabel, sortDate);
  };
  protoOf(DayEventDisplay).copy$default_jcf9g9_k$ = function (event, kind, days, elapsedDays, label, nextDate, repeatLabel, sortDate, $super) {
    event = event === VOID ? this.event_1 : event;
    kind = kind === VOID ? this.kind_1 : kind;
    days = days === VOID ? this.days_1 : days;
    elapsedDays = elapsedDays === VOID ? this.elapsedDays_1 : elapsedDays;
    label = label === VOID ? this.label_1 : label;
    nextDate = nextDate === VOID ? this.nextDate_1 : nextDate;
    repeatLabel = repeatLabel === VOID ? this.repeatLabel_1 : repeatLabel;
    sortDate = sortDate === VOID ? this.sortDate_1 : sortDate;
    return $super === VOID ? this.copy_4gdri7_k$(event, kind, days, elapsedDays, label, nextDate, repeatLabel, sortDate) : $super.copy_4gdri7_k$.call(this, event, kind, days, elapsedDays, label, nextDate, repeatLabel, sortDate);
  };
  protoOf(DayEventDisplay).toString = function () {
    return 'DayEventDisplay(event=' + this.event_1.toString() + ', kind=' + this.kind_1.toString() + ', days=' + this.days_1 + ', elapsedDays=' + this.elapsedDays_1 + ', label=' + this.label_1 + ', nextDate=' + this.nextDate_1 + ', repeatLabel=' + this.repeatLabel_1 + ', sortDate=' + this.sortDate_1 + ')';
  };
  protoOf(DayEventDisplay).hashCode = function () {
    var result = this.event_1.hashCode();
    result = imul(result, 31) + this.kind_1.hashCode() | 0;
    result = imul(result, 31) + this.days_1 | 0;
    result = imul(result, 31) + this.elapsedDays_1 | 0;
    result = imul(result, 31) + getStringHashCode(this.label_1) | 0;
    result = imul(result, 31) + (this.nextDate_1 == null ? 0 : getStringHashCode(this.nextDate_1)) | 0;
    result = imul(result, 31) + (this.repeatLabel_1 == null ? 0 : getStringHashCode(this.repeatLabel_1)) | 0;
    result = imul(result, 31) + getStringHashCode(this.sortDate_1) | 0;
    return result;
  };
  protoOf(DayEventDisplay).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof DayEventDisplay))
      return false;
    var tmp0_other_with_cast = other instanceof DayEventDisplay ? other : THROW_CCE();
    if (!this.event_1.equals(tmp0_other_with_cast.event_1))
      return false;
    if (!this.kind_1.equals(tmp0_other_with_cast.kind_1))
      return false;
    if (!(this.days_1 === tmp0_other_with_cast.days_1))
      return false;
    if (!(this.elapsedDays_1 === tmp0_other_with_cast.elapsedDays_1))
      return false;
    if (!(this.label_1 === tmp0_other_with_cast.label_1))
      return false;
    if (!(this.nextDate_1 == tmp0_other_with_cast.nextDate_1))
      return false;
    if (!(this.repeatLabel_1 == tmp0_other_with_cast.repeatLabel_1))
      return false;
    if (!(this.sortDate_1 === tmp0_other_with_cast.sortDate_1))
      return false;
    return true;
  };
  function DurationBreakdown(days, years, monthsAfterYears, daysAfterMonthsFromYears, months, daysAfterMonths) {
    this.days_1 = days;
    this.years_1 = years;
    this.monthsAfterYears_1 = monthsAfterYears;
    this.daysAfterMonthsFromYears_1 = daysAfterMonthsFromYears;
    this.months_1 = months;
    this.daysAfterMonths_1 = daysAfterMonths;
  }
  protoOf(DurationBreakdown).get_days_wokl28_k$ = function () {
    return this.days_1;
  };
  protoOf(DurationBreakdown).get_years_j1r3xp_k$ = function () {
    return this.years_1;
  };
  protoOf(DurationBreakdown).get_monthsAfterYears_lu0vw6_k$ = function () {
    return this.monthsAfterYears_1;
  };
  protoOf(DurationBreakdown).get_daysAfterMonthsFromYears_4jngv7_k$ = function () {
    return this.daysAfterMonthsFromYears_1;
  };
  protoOf(DurationBreakdown).get_months_gqmrjw_k$ = function () {
    return this.months_1;
  };
  protoOf(DurationBreakdown).get_daysAfterMonths_bwjv81_k$ = function () {
    return this.daysAfterMonths_1;
  };
  protoOf(DurationBreakdown).component1_7eebsc_k$ = function () {
    return this.days_1;
  };
  protoOf(DurationBreakdown).component2_7eebsb_k$ = function () {
    return this.years_1;
  };
  protoOf(DurationBreakdown).component3_7eebsa_k$ = function () {
    return this.monthsAfterYears_1;
  };
  protoOf(DurationBreakdown).component4_7eebs9_k$ = function () {
    return this.daysAfterMonthsFromYears_1;
  };
  protoOf(DurationBreakdown).component5_7eebs8_k$ = function () {
    return this.months_1;
  };
  protoOf(DurationBreakdown).component6_7eebs7_k$ = function () {
    return this.daysAfterMonths_1;
  };
  protoOf(DurationBreakdown).copy_ek662d_k$ = function (days, years, monthsAfterYears, daysAfterMonthsFromYears, months, daysAfterMonths) {
    return new DurationBreakdown(days, years, monthsAfterYears, daysAfterMonthsFromYears, months, daysAfterMonths);
  };
  protoOf(DurationBreakdown).copy$default_n8neu8_k$ = function (days, years, monthsAfterYears, daysAfterMonthsFromYears, months, daysAfterMonths, $super) {
    days = days === VOID ? this.days_1 : days;
    years = years === VOID ? this.years_1 : years;
    monthsAfterYears = monthsAfterYears === VOID ? this.monthsAfterYears_1 : monthsAfterYears;
    daysAfterMonthsFromYears = daysAfterMonthsFromYears === VOID ? this.daysAfterMonthsFromYears_1 : daysAfterMonthsFromYears;
    months = months === VOID ? this.months_1 : months;
    daysAfterMonths = daysAfterMonths === VOID ? this.daysAfterMonths_1 : daysAfterMonths;
    return $super === VOID ? this.copy_ek662d_k$(days, years, monthsAfterYears, daysAfterMonthsFromYears, months, daysAfterMonths) : $super.copy_ek662d_k$.call(this, days, years, monthsAfterYears, daysAfterMonthsFromYears, months, daysAfterMonths);
  };
  protoOf(DurationBreakdown).toString = function () {
    return 'DurationBreakdown(days=' + this.days_1 + ', years=' + this.years_1 + ', monthsAfterYears=' + this.monthsAfterYears_1 + ', daysAfterMonthsFromYears=' + this.daysAfterMonthsFromYears_1 + ', months=' + this.months_1 + ', daysAfterMonths=' + this.daysAfterMonths_1 + ')';
  };
  protoOf(DurationBreakdown).hashCode = function () {
    var result = this.days_1;
    result = imul(result, 31) + this.years_1 | 0;
    result = imul(result, 31) + this.monthsAfterYears_1 | 0;
    result = imul(result, 31) + this.daysAfterMonthsFromYears_1 | 0;
    result = imul(result, 31) + this.months_1 | 0;
    result = imul(result, 31) + this.daysAfterMonths_1 | 0;
    return result;
  };
  protoOf(DurationBreakdown).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof DurationBreakdown))
      return false;
    var tmp0_other_with_cast = other instanceof DurationBreakdown ? other : THROW_CCE();
    if (!(this.days_1 === tmp0_other_with_cast.days_1))
      return false;
    if (!(this.years_1 === tmp0_other_with_cast.years_1))
      return false;
    if (!(this.monthsAfterYears_1 === tmp0_other_with_cast.monthsAfterYears_1))
      return false;
    if (!(this.daysAfterMonthsFromYears_1 === tmp0_other_with_cast.daysAfterMonthsFromYears_1))
      return false;
    if (!(this.months_1 === tmp0_other_with_cast.months_1))
      return false;
    if (!(this.daysAfterMonths_1 === tmp0_other_with_cast.daysAfterMonths_1))
      return false;
    return true;
  };
  function DurationDisplay(value, unitLabel, rest) {
    this.value_1 = value;
    this.unitLabel_1 = unitLabel;
    this.rest_1 = rest;
  }
  protoOf(DurationDisplay).get_value_j01efc_k$ = function () {
    return this.value_1;
  };
  protoOf(DurationDisplay).get_unitLabel_ejr53_k$ = function () {
    return this.unitLabel_1;
  };
  protoOf(DurationDisplay).get_rest_wotlp9_k$ = function () {
    return this.rest_1;
  };
  protoOf(DurationDisplay).component1_7eebsc_k$ = function () {
    return this.value_1;
  };
  protoOf(DurationDisplay).component2_7eebsb_k$ = function () {
    return this.unitLabel_1;
  };
  protoOf(DurationDisplay).component3_7eebsa_k$ = function () {
    return this.rest_1;
  };
  protoOf(DurationDisplay).copy_hrd73x_k$ = function (value, unitLabel, rest) {
    return new DurationDisplay(value, unitLabel, rest);
  };
  protoOf(DurationDisplay).copy$default_6q0zq2_k$ = function (value, unitLabel, rest, $super) {
    value = value === VOID ? this.value_1 : value;
    unitLabel = unitLabel === VOID ? this.unitLabel_1 : unitLabel;
    rest = rest === VOID ? this.rest_1 : rest;
    return $super === VOID ? this.copy_hrd73x_k$(value, unitLabel, rest) : $super.copy_hrd73x_k$.call(this, value, unitLabel, rest);
  };
  protoOf(DurationDisplay).toString = function () {
    return 'DurationDisplay(value=' + this.value_1 + ', unitLabel=' + this.unitLabel_1 + ', rest=' + this.rest_1 + ')';
  };
  protoOf(DurationDisplay).hashCode = function () {
    var result = this.value_1;
    result = imul(result, 31) + getStringHashCode(this.unitLabel_1) | 0;
    result = imul(result, 31) + getStringHashCode(this.rest_1) | 0;
    return result;
  };
  protoOf(DurationDisplay).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof DurationDisplay))
      return false;
    var tmp0_other_with_cast = other instanceof DurationDisplay ? other : THROW_CCE();
    if (!(this.value_1 === tmp0_other_with_cast.value_1))
      return false;
    if (!(this.unitLabel_1 === tmp0_other_with_cast.unitLabel_1))
      return false;
    if (!(this.rest_1 === tmp0_other_with_cast.rest_1))
      return false;
    return true;
  };
  var DurationUnit_DAY_instance;
  var DurationUnit_MONTH_instance;
  var DurationUnit_YEAR_instance;
  function values_2() {
    return [DurationUnit_DAY_getInstance(), DurationUnit_MONTH_getInstance(), DurationUnit_YEAR_getInstance()];
  }
  function valueOf_2(value) {
    switch (value) {
      case 'DAY':
        return DurationUnit_DAY_getInstance();
      case 'MONTH':
        return DurationUnit_MONTH_getInstance();
      case 'YEAR':
        return DurationUnit_YEAR_getInstance();
      default:
        DurationUnit_initEntries();
        THROW_IAE('No enum constant value.');
        break;
    }
  }
  function get_entries_2() {
    if ($ENTRIES_2 == null)
      $ENTRIES_2 = enumEntries(values_2());
    return $ENTRIES_2;
  }
  var DurationUnit_entriesInitialized;
  function DurationUnit_initEntries() {
    if (DurationUnit_entriesInitialized)
      return Unit_getInstance();
    DurationUnit_entriesInitialized = true;
    DurationUnit_DAY_instance = new DurationUnit('DAY', 0);
    DurationUnit_MONTH_instance = new DurationUnit('MONTH', 1);
    DurationUnit_YEAR_instance = new DurationUnit('YEAR', 2);
  }
  var $ENTRIES_2;
  function DurationUnit(name, ordinal) {
    Enum.call(this, name, ordinal);
  }
  function RepeatUnit_NONE_getInstance() {
    RepeatUnit_initEntries();
    return RepeatUnit_NONE_instance;
  }
  function RepeatUnit_DAY_getInstance() {
    RepeatUnit_initEntries();
    return RepeatUnit_DAY_instance;
  }
  function RepeatUnit_WEEK_getInstance() {
    RepeatUnit_initEntries();
    return RepeatUnit_WEEK_instance;
  }
  function RepeatUnit_MONTH_getInstance() {
    RepeatUnit_initEntries();
    return RepeatUnit_MONTH_instance;
  }
  function RepeatUnit_YEAR_getInstance() {
    RepeatUnit_initEntries();
    return RepeatUnit_YEAR_instance;
  }
  function EventKind_COUNTDOWN_getInstance() {
    EventKind_initEntries();
    return EventKind_COUNTDOWN_instance;
  }
  function EventKind_ELAPSED_getInstance() {
    EventKind_initEntries();
    return EventKind_ELAPSED_instance;
  }
  function EventKind_TODAY_getInstance() {
    EventKind_initEntries();
    return EventKind_TODAY_instance;
  }
  function EventKind_REPEAT_getInstance() {
    EventKind_initEntries();
    return EventKind_REPEAT_instance;
  }
  function DurationUnit_DAY_getInstance() {
    DurationUnit_initEntries();
    return DurationUnit_DAY_instance;
  }
  function DurationUnit_MONTH_getInstance() {
    DurationUnit_initEntries();
    return DurationUnit_MONTH_instance;
  }
  function DurationUnit_YEAR_getInstance() {
    DurationUnit_initEntries();
    return DurationUnit_YEAR_instance;
  }
  function rank($this, e) {
    var tmp;
    switch (e.get_kind_wop7ml_k$().get_ordinal_ip24qg_k$()) {
      case 3:
      case 2:
        tmp = 0;
        break;
      case 0:
        tmp = 1;
        break;
      case 1:
        tmp = 2;
        break;
      default:
        noWhenBranchMatchedException();
        break;
    }
    return tmp;
  }
  function joinNonZero($this, parts) {
    // Inline function 'kotlin.collections.filter' call
    // Inline function 'kotlin.collections.filterTo' call
    var destination = ArrayList_init_$Create$();
    var tmp0_iterator = parts.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.domain.DateCalculator.joinNonZero.<anonymous>' call
      if (element.get_first_irdx8n_k$() > 0) {
        destination.add_utx5q5_k$(element);
      }
    }
    var tmp = destination;
    return joinToString(tmp, ' ', VOID, VOID, VOID, VOID, DateCalculator$joinNonZero$lambda);
  }
  function floorDiv($this, a, b) {
    var q = a.div_jun7gj_k$(b);
    if (a.xor_qzz94j_k$(b).compareTo_9jj042_k$(new Long(0, 0)) < 0 && !q.times_nfzjiw_k$(b).equals(a)) {
      q = q.dec_24n6_k$();
    }
    return q;
  }
  function floorMod($this, a, b) {
    return a.minus_mfbszm_k$(floorDiv($this, a, b).times_nfzjiw_k$(b));
  }
  function sam$kotlin_Comparator$0(function_0) {
    this.function_1 = function_0;
  }
  protoOf(sam$kotlin_Comparator$0).compare_bczr_k$ = function (a, b) {
    return this.function_1(a, b);
  };
  protoOf(sam$kotlin_Comparator$0).compare = function (a, b) {
    return this.compare_bczr_k$(a, b);
  };
  protoOf(sam$kotlin_Comparator$0).getFunctionDelegate_jtodtf_k$ = function () {
    return this.function_1;
  };
  protoOf(sam$kotlin_Comparator$0).equals = function (other) {
    var tmp;
    if (!(other == null) ? isInterface(other, Comparator) : false) {
      var tmp_0;
      if (!(other == null) ? isInterface(other, FunctionAdapter) : false) {
        tmp_0 = equals(this.getFunctionDelegate_jtodtf_k$(), other.getFunctionDelegate_jtodtf_k$());
      } else {
        tmp_0 = false;
      }
      tmp = tmp_0;
    } else {
      tmp = false;
    }
    return tmp;
  };
  protoOf(sam$kotlin_Comparator$0).hashCode = function () {
    return hashCode(this.getFunctionDelegate_jtodtf_k$());
  };
  function DateCalculator$sortEvents$lambda(a, b) {
    // Inline function 'kotlin.comparisons.compareValuesBy' call
    // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
    var tmp = b.get_starred_u3zcve_k$();
    // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
    var tmp$ret$1 = a.get_starred_u3zcve_k$();
    return compareValues(tmp, tmp$ret$1);
  }
  function DateCalculator$sortEvents$lambda_0($this) {
    return function (a, b) {
      var previousCompare = $this.compare(a, b);
      var tmp;
      if (!(previousCompare === 0)) {
        tmp = previousCompare;
      } else {
        // Inline function 'kotlin.comparisons.compareValuesBy' call
        // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
        var tmp_0 = rank(DateCalculator_getInstance(), a);
        // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
        var tmp$ret$1 = rank(DateCalculator_getInstance(), b);
        tmp = compareValues(tmp_0, tmp$ret$1);
      }
      return tmp;
    };
  }
  function DateCalculator$sortEvents$lambda_1($this) {
    return function (a, b) {
      var previousCompare = $this.compare(a, b);
      var tmp;
      if (!(previousCompare === 0)) {
        tmp = previousCompare;
      } else {
        // Inline function 'kotlin.comparisons.compareValuesBy' call
        // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
        var tmp_0;
        if (rank(DateCalculator_getInstance(), a) === 2) {
          // Inline function 'kotlin.text.reversed' call
          var this_0 = a.get_sortDate_4bt0i3_k$();
          tmp_0 = toString(reversed(isCharSequence(this_0) ? this_0 : THROW_CCE()));
        } else {
          tmp_0 = a.get_sortDate_4bt0i3_k$();
        }
        var tmp_1 = tmp_0;
        // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
        var tmp_2;
        if (rank(DateCalculator_getInstance(), b) === 2) {
          // Inline function 'kotlin.text.reversed' call
          var this_1 = b.get_sortDate_4bt0i3_k$();
          tmp_2 = toString(reversed(isCharSequence(this_1) ? this_1 : THROW_CCE()));
        } else {
          tmp_2 = b.get_sortDate_4bt0i3_k$();
        }
        var tmp$ret$3 = tmp_2;
        tmp = compareValues(tmp_1, tmp$ret$3);
      }
      return tmp;
    };
  }
  function DateCalculator$sortEvents$lambda_2($this) {
    return function (a, b) {
      var previousCompare = $this.compare(a, b);
      var tmp;
      if (!(previousCompare === 0)) {
        tmp = previousCompare;
      } else {
        // Inline function 'kotlin.comparisons.compareValuesBy' call
        // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
        var tmp_0 = a.get_id_kntnx8_k$();
        // Inline function 'com.numberourdays.app.domain.DateCalculator.sortEvents.<anonymous>' call
        var tmp$ret$1 = b.get_id_kntnx8_k$();
        tmp = compareValues(tmp_0, tmp$ret$1);
      }
      return tmp;
    };
  }
  function DateCalculator$joinNonZero$lambda(it) {
    return '' + it.get_first_irdx8n_k$() + ' ' + it.get_second_jf7fjx_k$();
  }
  function DateCalculator() {
    DateCalculator_instance = this;
  }
  protoOf(DateCalculator).today_1tt4xt_k$ = function () {
    return todayIn(System_getInstance(), Companion_getInstance_1().currentSystemDefault_9rduv_k$());
  };
  protoOf(DateCalculator).format_36xaz2_k$ = function (date) {
    return date.toString();
  };
  protoOf(DateCalculator).parse_6kqlp0_k$ = function (text) {
    // Inline function 'kotlin.Result.getOrNull' call
    // Inline function 'kotlin.runCatching' call
    var tmp;
    try {
      // Inline function 'kotlin.Companion.success' call
      Companion_getInstance();
      var tmp$ret$0;
      $l$block: {
        // Inline function 'com.numberourdays.app.domain.DateCalculator.parse.<anonymous>' call
        var tmp_0 = Companion_getInstance_2();
        var tmp_1;
        if (text == null) {
          tmp$ret$0 = null;
          break $l$block;
        } else {
          tmp_1 = text;
        }
        tmp$ret$0 = tmp_0.parse$default_krgov1_k$(tmp_1);
      }
      var value = tmp$ret$0;
      tmp = _Result___init__impl__xyqfz8(value);
    } catch ($p) {
      var tmp_2;
      if ($p instanceof Error) {
        var e = $p;
        // Inline function 'kotlin.Companion.failure' call
        Companion_getInstance();
        tmp_2 = _Result___init__impl__xyqfz8(createFailure(e));
      } else {
        throw $p;
      }
      tmp = tmp_2;
    }
    var this_0 = tmp;
    var tmp_3;
    if (_Result___get_isFailure__impl__jpiriv(this_0)) {
      tmp_3 = null;
    } else {
      var tmp_4 = _Result___get_value__impl__bjfvqg(this_0);
      tmp_3 = (tmp_4 == null ? true : !(tmp_4 == null)) ? tmp_4 : THROW_CCE();
    }
    return tmp_3;
  };
  protoOf(DateCalculator).isValidDate_5zjlfd_k$ = function (text) {
    var tmp;
    // Inline function 'kotlin.text.isNullOrBlank' call
    // Inline function 'kotlin.contracts.contract' call
    if (text == null || isBlank(text)) {
      tmp = true;
    } else {
      tmp = !Regex_init_$Create$('\\d{4}-\\d{2}-\\d{2}').matches_evli6i_k$(text);
    }
    if (tmp)
      return false;
    return !(this.parse_6kqlp0_k$(text) == null);
  };
  protoOf(DateCalculator).diffDays_ycdg84_k$ = function (from, to) {
    return toLong(to.toEpochDays_tp9eth_k$() - from.toEpochDays_tp9eth_k$() | 0);
  };
  protoOf(DateCalculator).daySpan_jk2lsq_k$ = function (from, to) {
    return to.toEpochDays_tp9eth_k$() - from.toEpochDays_tp9eth_k$() | 0;
  };
  protoOf(DateCalculator).formatRepeatLabel_xl8ueq_k$ = function (unit, interval) {
    if (unit.equals(RepeatUnit_NONE_getInstance()))
      return null;
    // Inline function 'kotlin.math.max' call
    var n = Math.max(1, interval);
    var tmp;
    switch (unit.get_ordinal_ip24qg_k$()) {
      case 1:
        tmp = '\u5929';
        break;
      case 2:
        tmp = '\u5468';
        break;
      case 3:
        tmp = '\u6708';
        break;
      case 4:
        tmp = '\u5E74';
        break;
      case 0:
        return null;
      default:
        noWhenBranchMatchedException();
        break;
    }
    var unitName = tmp;
    return n === 1 ? '\u6BCF' + unitName : '\u6BCF ' + n + ' ' + unitName;
  };
  protoOf(DateCalculator).resolveNextDate_eaqip4_k$ = function (anchorDate, unit, interval, ref) {
    var tmp0_elvis_lhs = this.parse_6kqlp0_k$(anchorDate);
    var tmp;
    if (tmp0_elvis_lhs == null) {
      return ref;
    } else {
      tmp = tmp0_elvis_lhs;
    }
    var anchor = tmp;
    if (unit.equals(RepeatUnit_NONE_getInstance()))
      return anchor;
    var next = anchor;
    // Inline function 'kotlin.math.max' call
    var step = Math.max(1, interval);
    var n = 0;
    var guard = 0;
    while (next.compareTo_phv9sr_k$(ref) < 0 && guard < 20000) {
      n = n + 1 | 0;
      var tmp_0;
      switch (unit.get_ordinal_ip24qg_k$()) {
        case 1:
          tmp_0 = plus(anchor, DatePeriod_init_$Create$(VOID, VOID, imul(n, step)));
          break;
        case 2:
          tmp_0 = plus(anchor, DatePeriod_init_$Create$(VOID, VOID, imul(imul(n, step), 7)));
          break;
        case 3:
          // Inline function 'kotlin.Long.times' call

          var tmp$ret$1 = toLong(n).times_nfzjiw_k$(toLong(step));
          tmp_0 = this.plusMonthsFromAnchor_o1angr_k$(anchor, tmp$ret$1);
          break;
        case 4:
          // Inline function 'kotlin.Long.times' call

          var tmp$ret$2 = toLong(n).times_nfzjiw_k$(toLong(step));
          tmp_0 = this.plusYearsFromAnchor_x3kfac_k$(anchor, tmp$ret$2);
          break;
        case 0:
          tmp_0 = next;
          break;
        default:
          noWhenBranchMatchedException();
          break;
      }
      next = tmp_0;
      guard = guard + 1 | 0;
    }
    return next;
  };
  protoOf(DateCalculator).resolveNextDate$default_lmub7b_k$ = function (anchorDate, unit, interval, ref, $super) {
    ref = ref === VOID ? this.today_1tt4xt_k$() : ref;
    return $super === VOID ? this.resolveNextDate_eaqip4_k$(anchorDate, unit, interval, ref) : $super.resolveNextDate_eaqip4_k$.call(this, anchorDate, unit, interval, ref);
  };
  protoOf(DateCalculator).plusMonthsFromAnchor_o1angr_k$ = function (anchor, monthsToAdd) {
    // Inline function 'kotlin.Long.plus' call
    var this_0 = numberToLong(anchor.get_year_woy26e_k$()).times_nfzjiw_k$(new Long(12, 0));
    var other = anchor.get_monthNumber_nb47ls_k$() - 1 | 0;
    var monthIndex = this_0.plus_r93sks_k$(toLong(other)).plus_r93sks_k$(monthsToAdd);
    var year = floorDiv(this, monthIndex, new Long(12, 0)).toInt_1tsl84_k$();
    var month = floorMod(this, monthIndex, new Long(12, 0)).toInt_1tsl84_k$() + 1 | 0;
    // Inline function 'kotlin.math.min' call
    var a = anchor.get_dayOfMonth_vblo3a_k$();
    var b = this.daysInMonth_tp53i4_k$(year, month);
    var day = Math.min(a, b);
    return LocalDate_init_$Create$(year, month, day);
  };
  protoOf(DateCalculator).plusYearsFromAnchor_x3kfac_k$ = function (anchor, yearsToAdd) {
    var year = anchor.get_year_woy26e_k$() + yearsToAdd.toInt_1tsl84_k$() | 0;
    // Inline function 'kotlin.math.min' call
    var a = anchor.get_dayOfMonth_vblo3a_k$();
    var b = this.daysInMonth_tp53i4_k$(year, anchor.get_monthNumber_nb47ls_k$());
    var day = Math.min(a, b);
    return LocalDate_init_$Create$(year, anchor.get_monthNumber_nb47ls_k$(), day);
  };
  protoOf(DateCalculator).daysInMonth_tp53i4_k$ = function (year, monthNumber) {
    var nextYear = monthNumber === 12 ? year + 1 | 0 : year;
    var nextMonth = monthNumber === 12 ? 1 : monthNumber + 1 | 0;
    return this.daySpan_jk2lsq_k$(LocalDate_init_$Create$(year, monthNumber, 1), LocalDate_init_$Create$(nextYear, nextMonth, 1));
  };
  protoOf(DateCalculator).computeDisplay_rjjayo_k$ = function (event, ref) {
    var tmp0_elvis_lhs = this.parse_6kqlp0_k$(event.get_date_wokkxj_k$());
    var anchor = tmp0_elvis_lhs == null ? ref : tmp0_elvis_lhs;
    var unit = event.get_repeatUnit_zunjs_k$();
    // Inline function 'kotlin.math.max' call
    var b = event.get_repeatInterval_awuft5_k$();
    var interval = Math.max(1, b);
    // Inline function 'kotlin.math.max' call
    var a = new Long(0, 0);
    var b_0 = this.diffDays_ycdg84_k$(anchor, ref);
    var elapsed = a.compareTo_9jj042_k$(b_0) >= 0 ? a : b_0;
    var repeatLabel = this.formatRepeatLabel_xl8ueq_k$(unit, interval);
    if (!unit.equals(RepeatUnit_NONE_getInstance())) {
      var next = this.resolveNextDate_eaqip4_k$(event.get_date_wokkxj_k$(), unit, interval, ref);
      // Inline function 'kotlin.math.max' call
      var a_0 = new Long(0, 0);
      var b_1 = this.diffDays_ycdg84_k$(ref, next);
      var toNext = a_0.compareTo_9jj042_k$(b_1) >= 0 ? a_0 : b_1;
      var isToday = toNext.equals(new Long(0, 0));
      return new DayEventDisplay(event, EventKind_REPEAT_getInstance(), toNext.toInt_1tsl84_k$(), elapsed.toInt_1tsl84_k$(), isToday ? '\u4ECA\u5929' : '\u8FD8\u6709 ' + toNext.toString() + ' \u5929', this.format_36xaz2_k$(next), repeatLabel, this.format_36xaz2_k$(next));
    }
    var pastOrFuture = this.diffDays_ycdg84_k$(anchor, ref).toInt_1tsl84_k$();
    var tmp;
    if (pastOrFuture === 0) {
      tmp = new DayEventDisplay(event, EventKind_TODAY_getInstance(), 0, 0, '\u4ECA\u5929', VOID, repeatLabel, event.get_date_wokkxj_k$());
    } else if (pastOrFuture < 0) {
      var remain = abs(pastOrFuture);
      tmp = new DayEventDisplay(event, EventKind_COUNTDOWN_getInstance(), remain, 0, '\u8FD8\u6709 ' + remain + ' \u5929', VOID, repeatLabel, event.get_date_wokkxj_k$());
    } else {
      tmp = new DayEventDisplay(event, EventKind_ELAPSED_getInstance(), pastOrFuture, pastOrFuture, '\u5DF2\u7ECF\u8FC7 ' + pastOrFuture + ' \u5929', VOID, repeatLabel, event.get_date_wokkxj_k$());
    }
    return tmp;
  };
  protoOf(DateCalculator).computeDisplay$default_yvxxv5_k$ = function (event, ref, $super) {
    ref = ref === VOID ? this.today_1tt4xt_k$() : ref;
    return $super === VOID ? this.computeDisplay_rjjayo_k$(event, ref) : $super.computeDisplay_rjjayo_k$.call(this, event, ref);
  };
  protoOf(DateCalculator).sortEvents_xxw6gz_k$ = function (list) {
    // Inline function 'kotlin.comparisons.thenBy' call
    // Inline function 'kotlin.comparisons.thenBy' call
    // Inline function 'kotlin.comparisons.thenBy' call
    // Inline function 'kotlin.comparisons.compareByDescending' call
    var tmp = DateCalculator$sortEvents$lambda;
    var this_0 = new sam$kotlin_Comparator$0(tmp);
    var tmp_0 = DateCalculator$sortEvents$lambda_0(this_0);
    var this_1 = new sam$kotlin_Comparator$0(tmp_0);
    var tmp_1 = DateCalculator$sortEvents$lambda_1(this_1);
    var this_2 = new sam$kotlin_Comparator$0(tmp_1);
    var tmp_2 = DateCalculator$sortEvents$lambda_2(this_2);
    var tmp$ret$3 = new sam$kotlin_Comparator$0(tmp_2);
    return sortedWith(list, tmp$ret$3);
  };
  protoOf(DateCalculator).pickNearest_r4can1_k$ = function (list) {
    if (list.isEmpty_y1axqb_k$())
      return null;
    // Inline function 'kotlin.collections.filter' call
    // Inline function 'kotlin.collections.filterTo' call
    var destination = ArrayList_init_$Create$();
    var tmp0_iterator = list.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.domain.DateCalculator.pickNearest.<anonymous>' call
      if ((element.get_kind_wop7ml_k$().equals(EventKind_REPEAT_getInstance()) || element.get_kind_wop7ml_k$().equals(EventKind_COUNTDOWN_getInstance())) && element.get_days_wokl28_k$() > 0) {
        destination.add_utx5q5_k$(element);
      }
    }
    var upcoming = destination;
    var tmp$ret$3;
    $l$block_0: {
      // Inline function 'kotlin.collections.minByOrNull' call
      var iterator = upcoming.iterator_jk1svi_k$();
      if (!iterator.hasNext_bitz1p_k$()) {
        tmp$ret$3 = null;
        break $l$block_0;
      }
      var minElem = iterator.next_20eer_k$();
      if (!iterator.hasNext_bitz1p_k$()) {
        tmp$ret$3 = minElem;
        break $l$block_0;
      }
      // Inline function 'com.numberourdays.app.domain.DateCalculator.pickNearest.<anonymous>' call
      var minValue = minElem.get_days_wokl28_k$();
      do {
        var e = iterator.next_20eer_k$();
        // Inline function 'com.numberourdays.app.domain.DateCalculator.pickNearest.<anonymous>' call
        var v = e.get_days_wokl28_k$();
        if (compareTo(minValue, v) > 0) {
          minElem = e;
          minValue = v;
        }
      }
       while (iterator.hasNext_bitz1p_k$());
      tmp$ret$3 = minElem;
    }
    var tmp0_elvis_lhs = tmp$ret$3;
    return tmp0_elvis_lhs == null ? first(list) : tmp0_elvis_lhs;
  };
  protoOf(DateCalculator).carouselList_qt2gd8_k$ = function (list) {
    if (list.isEmpty_y1axqb_k$())
      return emptyList();
    var tmp0_elvis_lhs = this.pickNearest_r4can1_k$(list);
    var tmp;
    if (tmp0_elvis_lhs == null) {
      return list;
    } else {
      tmp = tmp0_elvis_lhs;
    }
    var nearest = tmp;
    var tmp_0 = listOf_0(nearest);
    // Inline function 'kotlin.collections.filter' call
    // Inline function 'kotlin.collections.filterTo' call
    var destination = ArrayList_init_$Create$();
    var tmp0_iterator = list.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var element = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.domain.DateCalculator.carouselList.<anonymous>' call
      if (!(element.get_id_kntnx8_k$() === nearest.get_id_kntnx8_k$())) {
        destination.add_utx5q5_k$(element);
      }
    }
    return plus_0(tmp_0, destination);
  };
  protoOf(DateCalculator).convertDuration_rmswjd_k$ = function (from, to) {
    var start = from.compareTo_phv9sr_k$(to) < 0 ? from : to;
    var end = from.compareTo_phv9sr_k$(to) < 0 ? to : from;
    var years = end.get_year_woy26e_k$() - start.get_year_woy26e_k$() | 0;
    if (this.plusYearsFromAnchor_x3kfac_k$(start, toLong(years)).compareTo_phv9sr_k$(end) > 0) {
      years = years - 1 | 0;
    }
    var monthsBeforeRemainder = imul(years, 12);
    var monthsAfterYears = 0;
    while (monthsAfterYears < 1200 && this.plusMonthsFromAnchor_o1angr_k$(start, toLong((monthsBeforeRemainder + monthsAfterYears | 0) + 1 | 0)).compareTo_phv9sr_k$(end) <= 0) {
      monthsAfterYears = monthsAfterYears + 1 | 0;
    }
    var months = monthsBeforeRemainder + monthsAfterYears | 0;
    var milestone = this.plusMonthsFromAnchor_o1angr_k$(start, toLong(months));
    var daysAfterMonths = this.daySpan_jk2lsq_k$(milestone, end);
    return new DurationBreakdown(abs_0(this.diffDays_ycdg84_k$(start, end)).toInt_1tsl84_k$(), coerceAtLeast(years, 0), monthsAfterYears, daysAfterMonths, months, daysAfterMonths);
  };
  protoOf(DateCalculator).formatDuration_2avqn4_k$ = function (b, unit) {
    var tmp;
    switch (unit.get_ordinal_ip24qg_k$()) {
      case 0:
        tmp = new DurationDisplay(b.get_days_wokl28_k$(), '\u5929', '');
        break;
      case 1:
        var tmp_0;
        if (b.get_months_gqmrjw_k$() > 0) {
          tmp_0 = new DurationDisplay(b.get_months_gqmrjw_k$(), '\u6708', joinNonZero(this, listOf_0(to(b.get_daysAfterMonths_bwjv81_k$(), '\u5929'))));
        } else {
          tmp_0 = new DurationDisplay(b.get_daysAfterMonths_bwjv81_k$(), '\u5929', '');
        }

        tmp = tmp_0;
        break;
      case 2:
        tmp = b.get_years_j1r3xp_k$() > 0 ? new DurationDisplay(b.get_years_j1r3xp_k$(), '\u5E74', joinNonZero(this, listOf([to(b.get_monthsAfterYears_lu0vw6_k$(), '\u6708'), to(b.get_daysAfterMonthsFromYears_4jngv7_k$(), '\u5929')]))) : b.get_monthsAfterYears_lu0vw6_k$() > 0 ? new DurationDisplay(b.get_monthsAfterYears_lu0vw6_k$(), '\u6708', joinNonZero(this, listOf_0(to(b.get_daysAfterMonthsFromYears_4jngv7_k$(), '\u5929')))) : new DurationDisplay(b.get_daysAfterMonthsFromYears_4jngv7_k$(), '\u5929', '');
        break;
      default:
        noWhenBranchMatchedException();
        break;
    }
    return tmp;
  };
  protoOf(DateCalculator).detailTargetDate_43qj1g_k$ = function (event) {
    var tmp0_elvis_lhs = event.get_nextDate_8969rq_k$();
    var tmp1_elvis_lhs = this.parse_6kqlp0_k$(tmp0_elvis_lhs == null ? event.get_date_wokkxj_k$() : tmp0_elvis_lhs);
    return tmp1_elvis_lhs == null ? this.today_1tt4xt_k$() : tmp1_elvis_lhs;
  };
  protoOf(DateCalculator).validateForm_4lyt1m_k$ = function (title, date, unit, interval) {
    // Inline function 'kotlin.text.trim' call
    var t = toString(trim(isCharSequence(title) ? title : THROW_CCE()));
    // Inline function 'kotlin.text.isEmpty' call
    if (charSequenceLength(t) === 0)
      return '\u8BF7\u586B\u5199\u6807\u9898';
    if (t.length > 30)
      return '\u6807\u9898\u4E0D\u8D85\u8FC7 30 \u5B57';
    if (!this.isValidDate_5zjlfd_k$(date))
      return '\u8BF7\u9009\u62E9\u5408\u6CD5\u65E5\u671F';
    if (!unit.equals(RepeatUnit_NONE_getInstance())) {
      var n = interval;
      if (n < 1)
        return '\u91CD\u590D\u95F4\u9694\u81F3\u5C11\u4E3A 1';
      if (n > 999)
        return '\u91CD\u590D\u95F4\u9694\u8FC7\u5927';
    }
    return null;
  };
  var DateCalculator_instance;
  function DateCalculator_getInstance() {
    if (DateCalculator_instance == null)
      new DateCalculator();
    return DateCalculator_instance;
  }
  function sam$kotlin_Comparator$0_0(function_0) {
    this.function_1 = function_0;
  }
  protoOf(sam$kotlin_Comparator$0_0).compare_bczr_k$ = function (a, b) {
    return this.function_1(a, b);
  };
  protoOf(sam$kotlin_Comparator$0_0).compare = function (a, b) {
    return this.compare_bczr_k$(a, b);
  };
  protoOf(sam$kotlin_Comparator$0_0).getFunctionDelegate_jtodtf_k$ = function () {
    return this.function_1;
  };
  protoOf(sam$kotlin_Comparator$0_0).equals = function (other) {
    var tmp;
    if (!(other == null) ? isInterface(other, Comparator) : false) {
      var tmp_0;
      if (!(other == null) ? isInterface(other, FunctionAdapter) : false) {
        tmp_0 = equals(this.getFunctionDelegate_jtodtf_k$(), other.getFunctionDelegate_jtodtf_k$());
      } else {
        tmp_0 = false;
      }
      tmp = tmp_0;
    } else {
      tmp = false;
    }
    return tmp;
  };
  protoOf(sam$kotlin_Comparator$0_0).hashCode = function () {
    return hashCode(this.getFunctionDelegate_jtodtf_k$());
  };
  function HolidayCalculator$listRows$lambda(a, b) {
    // Inline function 'kotlin.comparisons.compareValuesBy' call
    // Inline function 'com.numberourdays.app.domain.HolidayCalculator.listRows.<anonymous>' call
    var tmp = a.ongoing_1 ? 0 : a.past_1 ? 2 : 1;
    // Inline function 'com.numberourdays.app.domain.HolidayCalculator.listRows.<anonymous>' call
    var tmp$ret$1 = b.ongoing_1 ? 0 : b.past_1 ? 2 : 1;
    return compareValues(tmp, tmp$ret$1);
  }
  function HolidayCalculator$listRows$lambda_0($this) {
    return function (a, b) {
      var previousCompare = $this.compare(a, b);
      var tmp;
      if (!(previousCompare === 0)) {
        tmp = previousCompare;
      } else {
        // Inline function 'com.numberourdays.app.domain.HolidayCalculator.listRows.<anonymous>' call
        tmp = a.past_1 ? b.period_1.get_start_iypx6h_k$().compareTo_phv9sr_k$(a.period_1.get_start_iypx6h_k$()) : a.period_1.get_start_iypx6h_k$().compareTo_phv9sr_k$(b.period_1.get_start_iypx6h_k$());
      }
      return tmp;
    };
  }
  function HolidayCalculator() {
    HolidayCalculator_instance = this;
  }
  protoOf(HolidayCalculator).nextOrOngoing_r1ila3_k$ = function (periods, today) {
    // Inline function 'kotlin.collections.find' call
    var tmp$ret$1;
    $l$block: {
      // Inline function 'kotlin.collections.firstOrNull' call
      var tmp0_iterator = periods.iterator_jk1svi_k$();
      while (tmp0_iterator.hasNext_bitz1p_k$()) {
        var element = tmp0_iterator.next_20eer_k$();
        // Inline function 'com.numberourdays.app.domain.HolidayCalculator.nextOrOngoing.<anonymous>' call
        if (today.compareTo_phv9sr_k$(element.get_start_iypx6h_k$()) >= 0 && today.compareTo_phv9sr_k$(element.get_end_18j6ha_k$()) <= 0) {
          tmp$ret$1 = element;
          break $l$block;
        }
      }
      tmp$ret$1 = null;
    }
    var ongoing = tmp$ret$1;
    if (!(ongoing == null)) {
      return new HolidayCountdown(ongoing, HolidayStatus_ONGOING_getInstance(), 0, '\u5047\u671F\u4E2D');
    }
    var tmp$ret$6;
    $l$block_1: {
      // Inline function 'kotlin.collections.minByOrNull' call
      // Inline function 'kotlin.collections.filter' call
      // Inline function 'kotlin.collections.filterTo' call
      var destination = ArrayList_init_$Create$();
      var tmp0_iterator_0 = periods.iterator_jk1svi_k$();
      while (tmp0_iterator_0.hasNext_bitz1p_k$()) {
        var element_0 = tmp0_iterator_0.next_20eer_k$();
        // Inline function 'com.numberourdays.app.domain.HolidayCalculator.nextOrOngoing.<anonymous>' call
        if (element_0.get_start_iypx6h_k$().compareTo_phv9sr_k$(today) > 0) {
          destination.add_utx5q5_k$(element_0);
        }
      }
      var iterator = destination.iterator_jk1svi_k$();
      if (!iterator.hasNext_bitz1p_k$()) {
        tmp$ret$6 = null;
        break $l$block_1;
      }
      var minElem = iterator.next_20eer_k$();
      if (!iterator.hasNext_bitz1p_k$()) {
        tmp$ret$6 = minElem;
        break $l$block_1;
      }
      // Inline function 'com.numberourdays.app.domain.HolidayCalculator.nextOrOngoing.<anonymous>' call
      var minValue = minElem.get_start_iypx6h_k$();
      do {
        var e = iterator.next_20eer_k$();
        // Inline function 'com.numberourdays.app.domain.HolidayCalculator.nextOrOngoing.<anonymous>' call
        var v = e.get_start_iypx6h_k$();
        if (compareTo(minValue, v) > 0) {
          minElem = e;
          minValue = v;
        }
      }
       while (iterator.hasNext_bitz1p_k$());
      tmp$ret$6 = minElem;
    }
    var tmp0_elvis_lhs = tmp$ret$6;
    var tmp;
    if (tmp0_elvis_lhs == null) {
      return null;
    } else {
      tmp = tmp0_elvis_lhs;
    }
    var upcoming = tmp;
    var days = DateCalculator_getInstance().daySpan_jk2lsq_k$(today, upcoming.get_start_iypx6h_k$());
    return new HolidayCountdown(upcoming, HolidayStatus_UPCOMING_getInstance(), days, days <= 0 ? '\u4ECA\u5929\u653E\u5047' : '\u8FD8\u6709 ' + days + ' \u5929');
  };
  protoOf(HolidayCalculator).listRows_hm59wt_k$ = function (periods, today) {
    // Inline function 'kotlin.collections.map' call
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList_init_$Create$_0(collectionSizeOrDefault(periods, 10));
    var tmp0_iterator = periods.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var item = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.domain.HolidayCalculator.listRows.<anonymous>' call
      var ongoing = today.compareTo_phv9sr_k$(item.get_start_iypx6h_k$()) >= 0 && today.compareTo_phv9sr_k$(item.get_end_18j6ha_k$()) <= 0;
      var past = item.get_end_18j6ha_k$().compareTo_phv9sr_k$(today) < 0;
      var days = DateCalculator_getInstance().daySpan_jk2lsq_k$(today, item.get_start_iypx6h_k$());
      var tmp$ret$0 = new HolidayListRow(item, ongoing, past, days, past ? DateCalculator_getInstance().daySpan_jk2lsq_k$(item.get_end_18j6ha_k$(), today) : 0);
      destination.add_utx5q5_k$(tmp$ret$0);
    }
    var tmp = destination;
    // Inline function 'kotlin.comparisons.thenComparator' call
    // Inline function 'kotlin.comparisons.compareBy' call
    var tmp_0 = HolidayCalculator$listRows$lambda;
    var this_0 = new sam$kotlin_Comparator$0_0(tmp_0);
    var tmp_1 = HolidayCalculator$listRows$lambda_0(this_0);
    var tmp$ret$4 = new sam$kotlin_Comparator$0_0(tmp_1);
    return sortedWith(tmp, tmp$ret$4);
  };
  var HolidayCalculator_instance;
  function HolidayCalculator_getInstance() {
    if (HolidayCalculator_instance == null)
      new HolidayCalculator();
    return HolidayCalculator_instance;
  }
  function HolidayListRow(period, ongoing, past, daysUntilStart, daysPastEnd) {
    daysPastEnd = daysPastEnd === VOID ? 0 : daysPastEnd;
    this.period_1 = period;
    this.ongoing_1 = ongoing;
    this.past_1 = past;
    this.daysUntilStart_1 = daysUntilStart;
    this.daysPastEnd_1 = daysPastEnd;
  }
  protoOf(HolidayListRow).get_period_i0c0qy_k$ = function () {
    return this.period_1;
  };
  protoOf(HolidayListRow).get_ongoing_kjhi0e_k$ = function () {
    return this.ongoing_1;
  };
  protoOf(HolidayListRow).get_past_wos8rf_k$ = function () {
    return this.past_1;
  };
  protoOf(HolidayListRow).get_daysUntilStart_5obrmk_k$ = function () {
    return this.daysUntilStart_1;
  };
  protoOf(HolidayListRow).get_daysPastEnd_9i49qv_k$ = function () {
    return this.daysPastEnd_1;
  };
  protoOf(HolidayListRow).get_vacationDays_esfqcd_k$ = function () {
    return DateCalculator_getInstance().daySpan_jk2lsq_k$(this.period_1.get_start_iypx6h_k$(), this.period_1.get_end_18j6ha_k$()) + 1 | 0;
  };
  protoOf(HolidayListRow).component1_7eebsc_k$ = function () {
    return this.period_1;
  };
  protoOf(HolidayListRow).component2_7eebsb_k$ = function () {
    return this.ongoing_1;
  };
  protoOf(HolidayListRow).component3_7eebsa_k$ = function () {
    return this.past_1;
  };
  protoOf(HolidayListRow).component4_7eebs9_k$ = function () {
    return this.daysUntilStart_1;
  };
  protoOf(HolidayListRow).component5_7eebs8_k$ = function () {
    return this.daysPastEnd_1;
  };
  protoOf(HolidayListRow).copy_tqbzys_k$ = function (period, ongoing, past, daysUntilStart, daysPastEnd) {
    return new HolidayListRow(period, ongoing, past, daysUntilStart, daysPastEnd);
  };
  protoOf(HolidayListRow).copy$default_c2zr0z_k$ = function (period, ongoing, past, daysUntilStart, daysPastEnd, $super) {
    period = period === VOID ? this.period_1 : period;
    ongoing = ongoing === VOID ? this.ongoing_1 : ongoing;
    past = past === VOID ? this.past_1 : past;
    daysUntilStart = daysUntilStart === VOID ? this.daysUntilStart_1 : daysUntilStart;
    daysPastEnd = daysPastEnd === VOID ? this.daysPastEnd_1 : daysPastEnd;
    return $super === VOID ? this.copy_tqbzys_k$(period, ongoing, past, daysUntilStart, daysPastEnd) : $super.copy_tqbzys_k$.call(this, period, ongoing, past, daysUntilStart, daysPastEnd);
  };
  protoOf(HolidayListRow).toString = function () {
    return 'HolidayListRow(period=' + this.period_1.toString() + ', ongoing=' + this.ongoing_1 + ', past=' + this.past_1 + ', daysUntilStart=' + this.daysUntilStart_1 + ', daysPastEnd=' + this.daysPastEnd_1 + ')';
  };
  protoOf(HolidayListRow).hashCode = function () {
    var result = this.period_1.hashCode();
    result = imul(result, 31) + getBooleanHashCode(this.ongoing_1) | 0;
    result = imul(result, 31) + getBooleanHashCode(this.past_1) | 0;
    result = imul(result, 31) + this.daysUntilStart_1 | 0;
    result = imul(result, 31) + this.daysPastEnd_1 | 0;
    return result;
  };
  protoOf(HolidayListRow).equals = function (other) {
    if (this === other)
      return true;
    if (!(other instanceof HolidayListRow))
      return false;
    var tmp0_other_with_cast = other instanceof HolidayListRow ? other : THROW_CCE();
    if (!this.period_1.equals(tmp0_other_with_cast.period_1))
      return false;
    if (!(this.ongoing_1 === tmp0_other_with_cast.ongoing_1))
      return false;
    if (!(this.past_1 === tmp0_other_with_cast.past_1))
      return false;
    if (!(this.daysUntilStart_1 === tmp0_other_with_cast.daysUntilStart_1))
      return false;
    if (!(this.daysPastEnd_1 === tmp0_other_with_cast.daysPastEnd_1))
      return false;
    return true;
  };
  function atHour($this, date, hourOfDay, minuteOfHour, zone) {
    return toInstant(LocalDateTime_init_$Create$(date, LocalTime_init_$Create$(hourOfDay, minuteOfHour)), zone).toEpochMilliseconds_82cfls_k$();
  }
  function ReminderTiming() {
    ReminderTiming_instance = this;
    this.HOUR_OF_DAY_1 = 9;
  }
  protoOf(ReminderTiming).get_HOUR_OF_DAY_9hfihy_k$ = function () {
    return this.HOUR_OF_DAY_1;
  };
  protoOf(ReminderTiming).nextReminderMillis_j1oz3k_k$ = function (anchorDate, unit, interval, now, hourOfDay, minuteOfHour, zone, notBefore) {
    var tmp0_elvis_lhs = DateCalculator_getInstance().parse_6kqlp0_k$(anchorDate);
    var tmp;
    if (tmp0_elvis_lhs == null) {
      return null;
    } else {
      tmp = tmp0_elvis_lhs;
    }
    var anchor = tmp;
    var zonedNow = toLocalDateTime(Companion_getInstance_3().fromEpochMilliseconds_e2resj_k$(now), zone);
    var today = zonedNow.get_date_wokkxj_k$();
    // Inline function 'kotlin.math.max' call
    var step = Math.max(1, interval);
    var tmp_0;
    if (unit.equals(RepeatUnit_NONE_getInstance())) {
      if (anchor.compareTo_phv9sr_k$(today) < 0)
        return null;
      tmp_0 = anchor;
    } else {
      var candidate = DateCalculator_getInstance().resolveNextDate_eaqip4_k$(anchorDate, unit, step, today);
      if (atHour(this, candidate, hourOfDay, minuteOfHour, zone).compareTo_9jj042_k$(notBefore) <= 0) {
        candidate = DateCalculator_getInstance().resolveNextDate_eaqip4_k$(anchorDate, unit, step, plus(today, DatePeriod_init_$Create$(VOID, VOID, 1)));
      }
      tmp_0 = candidate;
    }
    var target = tmp_0;
    var fireAt = atHour(this, target, hourOfDay, minuteOfHour, zone);
    if (fireAt.compareTo_9jj042_k$(notBefore) <= 0)
      return null;
    return fireAt;
  };
  protoOf(ReminderTiming).nextReminderMillis$default_aknhtm_k$ = function (anchorDate, unit, interval, now, hourOfDay, minuteOfHour, zone, notBefore, $super) {
    now = now === VOID ? System_getInstance().now_2cba_k$().toEpochMilliseconds_82cfls_k$() : now;
    hourOfDay = hourOfDay === VOID ? 9 : hourOfDay;
    minuteOfHour = minuteOfHour === VOID ? 0 : minuteOfHour;
    zone = zone === VOID ? Companion_getInstance_1().currentSystemDefault_9rduv_k$() : zone;
    notBefore = notBefore === VOID ? now : notBefore;
    return $super === VOID ? this.nextReminderMillis_j1oz3k_k$(anchorDate, unit, interval, now, hourOfDay, minuteOfHour, zone, notBefore) : $super.nextReminderMillis_j1oz3k_k$.call(this, anchorDate, unit, interval, now, hourOfDay, minuteOfHour, zone, notBefore);
  };
  protoOf(ReminderTiming).nextReminderMillisUtc_mitu5t_k$ = function (anchorDate, unit, interval, now, hourOfDay, minuteOfHour, notBefore) {
    return this.nextReminderMillis_j1oz3k_k$(anchorDate, unit, interval, now, hourOfDay, minuteOfHour, Companion_getInstance_1().get_UTC_18jizp_k$(), notBefore);
  };
  protoOf(ReminderTiming).nextReminderMillisUtc$default_8jitxt_k$ = function (anchorDate, unit, interval, now, hourOfDay, minuteOfHour, notBefore, $super) {
    hourOfDay = hourOfDay === VOID ? 9 : hourOfDay;
    minuteOfHour = minuteOfHour === VOID ? 0 : minuteOfHour;
    notBefore = notBefore === VOID ? now : notBefore;
    return $super === VOID ? this.nextReminderMillisUtc_mitu5t_k$(anchorDate, unit, interval, now, hourOfDay, minuteOfHour, notBefore) : $super.nextReminderMillisUtc_mitu5t_k$.call(this, anchorDate, unit, interval, now, hourOfDay, minuteOfHour, notBefore);
  };
  var ReminderTiming_instance;
  function ReminderTiming_getInstance() {
    if (ReminderTiming_instance == null)
      new ReminderTiming();
    return ReminderTiming_instance;
  }
  function _get_json__d8whur_0($this) {
    return $this.json_1;
  }
  function unwrap($this, raw) {
    // Inline function 'kotlin.text.trim' call
    var t = toString(trim(isCharSequence(raw) ? raw : THROW_CCE()));
    return startsWith(t, '[') ? t : '[' + t + ']';
  }
  function encodeDisplay($this, d) {
    var tmp0_safe_receiver = d.get_nextDate_8969rq_k$();
    var tmp;
    if (tmp0_safe_receiver == null) {
      tmp = null;
    } else {
      // Inline function 'kotlin.let' call
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'com.numberourdays.app.weapp.WeappDomain.encodeDisplay.<anonymous>' call
      tmp = '"' + tmp0_safe_receiver + '"';
    }
    var tmp1_elvis_lhs = tmp;
    var next = tmp1_elvis_lhs == null ? 'null' : tmp1_elvis_lhs;
    var tmp2_safe_receiver = d.get_repeatLabel_v0kvo0_k$();
    var tmp_0;
    if (tmp2_safe_receiver == null) {
      tmp_0 = null;
    } else {
      // Inline function 'kotlin.let' call
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'com.numberourdays.app.weapp.WeappDomain.encodeDisplay.<anonymous>' call
      tmp_0 = '"' + replace(tmp2_safe_receiver, '"', '\\"') + '"';
    }
    var tmp3_elvis_lhs = tmp_0;
    var repeat = tmp3_elvis_lhs == null ? 'null' : tmp3_elvis_lhs;
    var tmp4_safe_receiver = d.get_note_wor96j_k$();
    var tmp_1;
    if (tmp4_safe_receiver == null) {
      tmp_1 = null;
    } else {
      // Inline function 'kotlin.let' call
      // Inline function 'kotlin.contracts.contract' call
      // Inline function 'com.numberourdays.app.weapp.WeappDomain.encodeDisplay.<anonymous>' call
      tmp_1 = '"' + replace(replace(tmp4_safe_receiver, '"', '\\"'), '\n', '\\n') + '"';
    }
    var tmp5_elvis_lhs = tmp_1;
    var note = tmp5_elvis_lhs == null ? 'null' : tmp5_elvis_lhs;
    var title = replace(d.get_title_iz32un_k$(), '"', '\\"');
    return replace(trimIndent('\n            {"id":"' + d.get_id_kntnx8_k$() + '","title":"' + title + '","date":"' + d.get_date_wokkxj_k$() + '","kind":"' + d.get_kind_wop7ml_k$().get_name_woqyms_k$() + '",\n             "days":' + d.get_days_wokl28_k$() + ',"elapsedDays":' + d.get_elapsedDays_5v4xai_k$() + ',"label":"' + replace(d.get_label_iuj8p7_k$(), '"', '\\"') + '",\n             "nextDate":' + next + ',"repeatLabel":' + repeat + ',"starred":' + d.get_starred_u3zcve_k$() + ',\n             "remindEnabled":' + d.get_remindEnabled_8nmteb_k$() + ',"remindHour":' + d.get_remindHour_muku0e_k$() + ',"remindMinute":' + d.get_remindMinute_7gj5fi_k$() + ',\n             "note":' + note + '}\n        '), '\n', '');
  }
  function WeappDomain$json$lambda($this$Json) {
    $this$Json.set_ignoreUnknownKeys_pzvtne_k$(true);
    $this$Json.set_encodeDefaults_c5evsg_k$(true);
    $this$Json.set_explicitNulls_mtj2ai_k$(true);
    return Unit_getInstance();
  }
  function WeappDomain$sortDisplaysJson$lambda(it) {
    return encodeDisplay(WeappDomain_getInstance(), it);
  }
  function WeappDomain$parseBackup$lambda(it) {
    return DayEventCodec_getInstance().toJson_1ebj9p_k$(it);
  }
  function WeappDomain$mergeJson$lambda(it) {
    return DayEventCodec_getInstance().toJson_1ebj9p_k$(it);
  }
  function WeappDomain() {
    WeappDomain_instance = this;
    var tmp = this;
    tmp.json_1 = Json(VOID, WeappDomain$json$lambda);
  }
  protoOf(WeappDomain).today = function () {
    return DateCalculator_getInstance().format_36xaz2_k$(DateCalculator_getInstance().today_1tt4xt_k$());
  };
  protoOf(WeappDomain).isValidDate = function (text) {
    return DateCalculator_getInstance().isValidDate_5zjlfd_k$(text);
  };
  protoOf(WeappDomain).parseDate = function (text) {
    var d = DateCalculator_getInstance().parse_6kqlp0_k$(text);
    return d == null ? '' : DateCalculator_getInstance().format_36xaz2_k$(d);
  };
  protoOf(WeappDomain).createId = function () {
    return createEventId_0();
  };
  protoOf(WeappDomain).validateForm = function (title, date, unit, interval) {
    var u = Companion_getInstance_5().from_1jvler_k$(unit);
    var tmp0_elvis_lhs = DateCalculator_getInstance().validateForm_4lyt1m_k$(title, date, u, interval);
    return tmp0_elvis_lhs == null ? '' : tmp0_elvis_lhs;
  };
  protoOf(WeappDomain).computeDisplayJson_brlugd_k$ = function (eventJson, todayIso) {
    var tmp0_elvis_lhs = DayEventCodec_getInstance().migrate_w3le1o_k$(eventJson);
    var tmp;
    if (tmp0_elvis_lhs == null) {
      return '';
    } else {
      tmp = tmp0_elvis_lhs;
    }
    var e = tmp;
    var tmp1_elvis_lhs = DateCalculator_getInstance().parse_6kqlp0_k$(todayIso);
    var ref = tmp1_elvis_lhs == null ? DateCalculator_getInstance().today_1tt4xt_k$() : tmp1_elvis_lhs;
    var d = DateCalculator_getInstance().computeDisplay_rjjayo_k$(e, ref);
    return encodeDisplay(this, d);
  };
  protoOf(WeappDomain).computeDisplayJson = function (eventJson, todayIso, $super) {
    todayIso = todayIso === VOID ? this.today() : todayIso;
    return this.computeDisplayJson_brlugd_k$(eventJson, todayIso);
  };
  protoOf(WeappDomain).sortDisplaysJson_vdir0p_k$ = function (eventsJson, todayIso) {
    var tmp = DayEventCodec_getInstance();
    var tmp_0;
    // Inline function 'kotlin.text.trim' call
    var tmp$ret$0 = toString(trim(isCharSequence(eventsJson) ? eventsJson : THROW_CCE()));
    if (startsWith(tmp$ret$0, '[')) {
      tmp_0 = eventsJson;
    } else {
      tmp_0 = '[' + eventsJson + ']';
    }
    var events = tmp.parseEventsArray_lu4k7h_k$(tmp_0);
    var tmp_1;
    if (events.isEmpty_y1axqb_k$()) {
      var pair = DayEventCodec_getInstance().parseBackup_xtnnyd_k$(eventsJson);
      tmp_1 = pair.get_first_irdx8n_k$();
    } else {
      tmp_1 = events;
    }
    var list = tmp_1;
    var tmp0_elvis_lhs = DateCalculator_getInstance().parse_6kqlp0_k$(todayIso);
    var ref = tmp0_elvis_lhs == null ? DateCalculator_getInstance().today_1tt4xt_k$() : tmp0_elvis_lhs;
    var tmp_2 = DateCalculator_getInstance();
    // Inline function 'kotlin.collections.map' call
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList_init_$Create$_0(collectionSizeOrDefault(list, 10));
    var tmp0_iterator = list.iterator_jk1svi_k$();
    while (tmp0_iterator.hasNext_bitz1p_k$()) {
      var item = tmp0_iterator.next_20eer_k$();
      // Inline function 'com.numberourdays.app.weapp.WeappDomain.sortDisplaysJson.<anonymous>' call
      var tmp$ret$1 = DateCalculator_getInstance().computeDisplay_rjjayo_k$(item, ref);
      destination.add_utx5q5_k$(tmp$ret$1);
    }
    var displays = tmp_2.sortEvents_xxw6gz_k$(destination);
    return '[' + joinToString(displays, ',', VOID, VOID, VOID, VOID, WeappDomain$sortDisplaysJson$lambda) + ']';
  };
  protoOf(WeappDomain).sortDisplaysJson = function (eventsJson, todayIso, $super) {
    todayIso = todayIso === VOID ? this.today() : todayIso;
    return this.sortDisplaysJson_vdir0p_k$(eventsJson, todayIso);
  };
  protoOf(WeappDomain).serialize = function (eventsJson) {
    var tmp = DayEventCodec_getInstance();
    var tmp_0;
    // Inline function 'kotlin.text.trim' call
    var tmp$ret$0 = toString(trim(isCharSequence(eventsJson) ? eventsJson : THROW_CCE()));
    if (startsWith(tmp$ret$0, '[')) {
      tmp_0 = eventsJson;
    } else {
      tmp_0 = '[' + eventsJson + ']';
    }
    var events = tmp.parseEventsArray_lu4k7h_k$(tmp_0);
    return DayEventCodec_getInstance().serialize_7sr98y_k$(events);
  };
  protoOf(WeappDomain).parseBackup = function (text) {
    var _destruct__k2r9zo = DayEventCodec_getInstance().parseBackup_xtnnyd_k$(text);
    var events = _destruct__k2r9zo.component1_7eebsc_k$();
    var note = _destruct__k2r9zo.component2_7eebsb_k$();
    var arr = joinToString(events, ',', '[', ']', VOID, VOID, WeappDomain$parseBackup$lambda);
    var tmp;
    if (note == null) {
      tmp = 'null';
    } else {
      // Inline function 'kotlinx.serialization.encodeToString' call
      var this_0 = this.json_1;
      // Inline function 'kotlinx.serialization.serializer' call
      var this_1 = this_0.get_serializersModule_piitvg_k$();
      // Inline function 'kotlinx.serialization.internal.cast' call
      var this_2 = serializer(this_1, createKType(PrimitiveClasses_getInstance().get_stringClass_bik2gy_k$(), arrayOf([]), false));
      var tmp$ret$1 = isInterface(this_2, KSerializer) ? this_2 : THROW_CCE();
      tmp = this_0.encodeToString_k0apqx_k$(tmp$ret$1, note);
    }
    var noteJson = tmp;
    return '{"events":' + arr + ',"note":' + noteJson + '}';
  };
  protoOf(WeappDomain).mergeJson = function (localJson, incomingJson) {
    var local = DayEventCodec_getInstance().parseEventsArray_lu4k7h_k$(unwrap(this, localJson));
    var incoming = DayEventCodec_getInstance().parseEventsArray_lu4k7h_k$(unwrap(this, incomingJson));
    var merged = DayEventCodec_getInstance().merge_ri0zj8_k$(local, incoming);
    return joinToString(merged, ',', '[', ']', VOID, VOID, WeappDomain$mergeJson$lambda);
  };
  var WeappDomain_instance;
  function WeappDomain_getInstance() {
    if (WeappDomain_instance == null)
      new WeappDomain();
    return WeappDomain_instance;
  }
  function createEventId_0() {
    return createEventId();
  }
  //region block: post-declaration
  protoOf($serializer).typeParametersSerializers_fr94fx_k$ = typeParametersSerializers;
  //endregion
  //region block: exports
  function $jsExportAll$(_) {
    var $com = _.com || (_.com = {});
    var $com$numberourdays = $com.numberourdays || ($com.numberourdays = {});
    var $com$numberourdays$app = $com$numberourdays.app || ($com$numberourdays.app = {});
    var $com$numberourdays$app$weapp = $com$numberourdays$app.weapp || ($com$numberourdays$app.weapp = {});
    defineProp($com$numberourdays$app$weapp, 'WeappDomain', WeappDomain_getInstance);
  }
  $jsExportAll$(_);
  //endregion
  return _;
}(module.exports, require('./kotlin-kotlin-stdlib.js'), require('./kotlinx-serialization-kotlinx-serialization-core.js'), require('./Kotlin-DateTime-library-kotlinx-datetime.js'), require('./kotlinx-serialization-kotlinx-serialization-json.js')));

//# sourceMappingURL=NumberOurDays-shared.js.map
