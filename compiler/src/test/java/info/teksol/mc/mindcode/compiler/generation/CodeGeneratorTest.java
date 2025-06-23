package info.teksol.mc.mindcode.compiler.generation;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static info.teksol.mc.mindcode.logic.opcodes.Opcode.*;

@NullMarked
class CodeGeneratorTest extends AbstractCodeGeneratorTest {

    @Test
    void compilesVeryLargeExpressions() {
        assertCompiles("""
                print(bank1[0] == bank1[1] && bank1[1] == bank1[2] && bank1[2] == bank1[3] && bank1[3] == bank1[4]
                    && bank1[4] == bank1[5] && bank1[5] == bank1[6] && bank1[6] == bank1[7] && bank1[7] == bank1[8]
                    && bank1[8] == bank1[9] && bank1[9] == bank1[10] && bank1[10] == bank1[11] && bank1[11] == bank1[12]
                    && bank1[12] == bank1[13] && bank1[13] == bank1[14] && bank1[14] == bank1[15] && bank1[15] == bank1[16]
                    && bank1[16] == bank1[17] && bank1[17] == bank1[18] && bank1[18] == bank1[19] && bank1[19] == bank1[20]
                    && bank1[20] == bank1[21] && bank1[21] == bank1[22] && bank1[22] == bank1[23] && bank1[23] == bank1[24]
                    && bank1[24] == bank1[25] && bank1[25] == bank1[26] && bank1[26] == bank1[27] && bank1[27] == bank1[28]
                    && bank1[28] == bank1[29] && bank1[29] == bank1[30] && bank1[30] == bank1[31] && bank1[31] == bank1[32]
                    && bank1[32] == bank1[33] && bank1[33] == bank1[34] && bank1[34] == bank1[35] && bank1[35] == bank1[36]
                    && bank1[36] == bank1[37] && bank1[37] == bank1[38] && bank1[38] == bank1[39] && bank1[39] == bank1[40]
                    && bank1[40] == bank1[41] && bank1[41] == bank1[42] && bank1[42] == bank1[43] && bank1[43] == bank1[44]
                    && bank1[44] == bank1[45] && bank1[45] == bank1[46] && bank1[46] == bank1[47] && bank1[47] == bank1[48]
                    && bank1[48] == bank1[49] && bank1[49] == bank1[50] && bank1[50] == bank1[51] && bank1[51] == bank1[52]
                    && bank1[52] == bank1[53] && bank1[53] == bank1[54] && bank1[54] == bank1[55] && bank1[55] == bank1[56]
                    && bank1[56] == bank1[57] && bank1[57] == bank1[58] && bank1[58] == bank1[59] && bank1[59] == bank1[60]
                    && bank1[60] == bank1[61] && bank1[61] == bank1[62] && bank1[62] == bank1[63] && bank1[63] == bank1[64]
                    && bank1[64] == bank1[65] && bank1[65] == bank1[66] && bank1[66] == bank1[67] && bank1[67] == bank1[68]
                    && bank1[68] == bank1[69] && bank1[69] == bank1[70] && bank1[70] == bank1[71] && bank1[71] == bank1[72]
                    && bank1[72] == bank1[73] && bank1[73] == bank1[74] && bank1[74] == bank1[75] && bank1[75] == bank1[76]
                    && bank1[76] == bank1[77] && bank1[77] == bank1[78] && bank1[78] == bank1[79] && bank1[79] == bank1[80]
                    && bank1[80] == bank1[81] && bank1[81] == bank1[82] && bank1[82] == bank1[83] && bank1[83] == bank1[84]
                    && bank1[84] == bank1[85] && bank1[85] == bank1[86] && bank1[86] == bank1[87] && bank1[87] == bank1[88]
                    && bank1[88] == bank1[89] && bank1[89] == bank1[90] && bank1[90] == bank1[91] && bank1[91] == bank1[92]
                    && bank1[92] == bank1[93] && bank1[93] == bank1[94] && bank1[94] == bank1[95] && bank1[95] == bank1[96]
                    && bank1[96] == bank1[97] && bank1[97] == bank1[98] && bank1[98] == bank1[99] && bank1[99] == bank1[100]
                    && bank1[100] == bank1[101] && bank1[101] == bank1[102] && bank1[102] == bank1[103] && bank1[103] == bank1[104]
                    && bank1[104] == bank1[105] && bank1[105] == bank1[106] && bank1[106] == bank1[107] && bank1[107] == bank1[108]
                    && bank1[108] == bank1[109] && bank1[109] == bank1[110] && bank1[110] == bank1[111] && bank1[111] == bank1[112]
                    && bank1[112] == bank1[113] && bank1[113] == bank1[114] && bank1[114] == bank1[115] && bank1[115] == bank1[116]
                    && bank1[116] == bank1[117] && bank1[117] == bank1[118] && bank1[118] == bank1[119] && bank1[119] == bank1[120]
                    && bank1[120] == bank1[121] && bank1[121] == bank1[122] && bank1[122] == bank1[123] && bank1[123] == bank1[124]
                    && bank1[124] == bank1[125] && bank1[125] == bank1[126] && bank1[126] == bank1[127] && bank1[127] == bank1[128]
                    && bank1[128] == bank1[129] && bank1[129] == bank1[130] && bank1[130] == bank1[131] && bank1[131] == bank1[132]
                    && bank1[132] == bank1[133] && bank1[133] == bank1[134] && bank1[134] == bank1[135] && bank1[135] == bank1[136]
                    && bank1[136] == bank1[137] && bank1[137] == bank1[138] && bank1[138] == bank1[139] && bank1[139] == bank1[140]
                    && bank1[140] == bank1[141] && bank1[141] == bank1[142] && bank1[142] == bank1[143] && bank1[143] == bank1[144]
                    && bank1[144] == bank1[145] && bank1[145] == bank1[146] && bank1[146] == bank1[147] && bank1[147] == bank1[148]
                    && bank1[148] == bank1[149] && bank1[149] == bank1[150] && bank1[150] == bank1[151] && bank1[151] == bank1[152]
                    && bank1[152] == bank1[153] && bank1[153] == bank1[154] && bank1[154] == bank1[155] && bank1[155] == bank1[156]
                    && bank1[156] == bank1[157] && bank1[157] == bank1[158] && bank1[158] == bank1[159] && bank1[159] == bank1[160]
                    && bank1[160] == bank1[161] && bank1[161] == bank1[162] && bank1[162] == bank1[163] && bank1[163] == bank1[164]
                    && bank1[164] == bank1[165] && bank1[165] == bank1[166] && bank1[166] == bank1[167] && bank1[167] == bank1[168]
                    && bank1[168] == bank1[169] && bank1[169] == bank1[170] && bank1[170] == bank1[171] && bank1[171] == bank1[172]
                    && bank1[172] == bank1[173] && bank1[173] == bank1[174] && bank1[174] == bank1[175] && bank1[175] == bank1[176]
                    && bank1[176] == bank1[177] && bank1[177] == bank1[178] && bank1[178] == bank1[179] && bank1[179] == bank1[180]
                    && bank1[180] == bank1[181] && bank1[181] == bank1[182] && bank1[182] == bank1[183] && bank1[183] == bank1[184]
                    && bank1[184] == bank1[185] && bank1[185] == bank1[186] && bank1[186] == bank1[187] && bank1[187] == bank1[188]
                    && bank1[188] == bank1[189] && bank1[189] == bank1[190] && bank1[190] == bank1[191] && bank1[191] == bank1[192]
                    && bank1[192] == bank1[193] && bank1[193] == bank1[194] && bank1[194] == bank1[195] && bank1[195] == bank1[196]
                    && bank1[196] == bank1[197] && bank1[197] == bank1[198] && bank1[198] == bank1[199] && bank1[199] == bank1[200]
                    && bank1[200] == bank1[201] && bank1[201] == bank1[202] && bank1[202] == bank1[203] && bank1[203] == bank1[204]
                    && bank1[204] == bank1[205] && bank1[205] == bank1[206] && bank1[206] == bank1[207] && bank1[207] == bank1[208]
                    && bank1[208] == bank1[209] && bank1[209] == bank1[210] && bank1[210] == bank1[211] && bank1[211] == bank1[212]
                    && bank1[212] == bank1[213] && bank1[213] == bank1[214] && bank1[214] == bank1[215] && bank1[215] == bank1[216]
                    && bank1[216] == bank1[217] && bank1[217] == bank1[218] && bank1[218] == bank1[219] && bank1[219] == bank1[220]
                    && bank1[220] == bank1[221] && bank1[221] == bank1[222] && bank1[222] == bank1[223] && bank1[223] == bank1[224]
                    && bank1[224] == bank1[225] && bank1[225] == bank1[226] && bank1[226] == bank1[227] && bank1[227] == bank1[228]
                    && bank1[228] == bank1[229] && bank1[229] == bank1[230] && bank1[230] == bank1[231] && bank1[231] == bank1[232]
                    && bank1[232] == bank1[233] && bank1[233] == bank1[234] && bank1[234] == bank1[235] && bank1[235] == bank1[236]
                    && bank1[236] == bank1[237] && bank1[237] == bank1[238] && bank1[238] == bank1[239] && bank1[239] == bank1[240]
                    && bank1[240] == bank1[241] && bank1[241] == bank1[242] && bank1[242] == bank1[243] && bank1[243] == bank1[244]
                    && bank1[244] == bank1[245] && bank1[245] == bank1[246] && bank1[246] == bank1[247] && bank1[247] == bank1[248]
                    && bank1[248] == bank1[249] && bank1[249] == bank1[250] && bank1[250] == bank1[251] && bank1[251] == bank1[252]
                    && bank1[252] == bank1[253] && bank1[253] == bank1[254] && bank1[254] == bank1[255] && bank1[255] == bank1[256]
                    && bank1[256] == bank1[257] && bank1[257] == bank1[258] && bank1[258] == bank1[259] && bank1[259] == bank1[260]
                    && bank1[260] == bank1[261] && bank1[261] == bank1[262] && bank1[262] == bank1[263] && bank1[263] == bank1[264]
                    && bank1[264] == bank1[265] && bank1[265] == bank1[266] && bank1[266] == bank1[267] && bank1[267] == bank1[268]
                    && bank1[268] == bank1[269] && bank1[269] == bank1[270] && bank1[270] == bank1[271] && bank1[271] == bank1[272]
                    && bank1[272] == bank1[273] && bank1[273] == bank1[274] && bank1[274] == bank1[275] && bank1[275] == bank1[276]
                    && bank1[276] == bank1[277] && bank1[277] == bank1[278] && bank1[278] == bank1[279] && bank1[279] == bank1[280]
                    && bank1[280] == bank1[281] && bank1[281] == bank1[282] && bank1[282] == bank1[283] && bank1[283] == bank1[284]
                    && bank1[284] == bank1[285] && bank1[285] == bank1[286] && bank1[286] == bank1[287] && bank1[287] == bank1[288]
                    && bank1[288] == bank1[289] && bank1[289] == bank1[290] && bank1[290] == bank1[291] && bank1[291] == bank1[292]
                    && bank1[292] == bank1[293] && bank1[293] == bank1[294] && bank1[294] == bank1[295] && bank1[295] == bank1[296]
                    && bank1[296] == bank1[297] && bank1[297] == bank1[298] && bank1[298] == bank1[299] && bank1[299] == bank1[300]
                    && bank1[300] == bank1[301] && bank1[301] == bank1[302] && bank1[302] == bank1[303] && bank1[303] == bank1[304]
                    && bank1[304] == bank1[305] && bank1[305] == bank1[306] && bank1[306] == bank1[307] && bank1[307] == bank1[308]
                    && bank1[308] == bank1[309] && bank1[309] == bank1[310] && bank1[310] == bank1[311] && bank1[311] == bank1[312]
                    && bank1[312] == bank1[313] && bank1[313] == bank1[314] && bank1[314] == bank1[315] && bank1[315] == bank1[316]
                    && bank1[316] == bank1[317] && bank1[317] == bank1[318] && bank1[318] == bank1[319] && bank1[319] == bank1[320]
                    && bank1[320] == bank1[321] && bank1[321] == bank1[322] && bank1[322] == bank1[323] && bank1[323] == bank1[324]
                    && bank1[324] == bank1[325] && bank1[325] == bank1[326] && bank1[326] == bank1[327] && bank1[327] == bank1[328]
                    && bank1[328] == bank1[329] && bank1[329] == bank1[330] && bank1[330] == bank1[331] && bank1[331] == bank1[332]
                    && bank1[332] == bank1[333] && bank1[333] == bank1[334] && bank1[334] == bank1[335] && bank1[335] == bank1[336]
                    && bank1[336] == bank1[337] && bank1[337] == bank1[338] && bank1[338] == bank1[339] && bank1[339] == bank1[340]
                    && bank1[340] == bank1[341] && bank1[341] == bank1[342] && bank1[342] == bank1[343] && bank1[343] == bank1[344]
                    && bank1[344] == bank1[345] && bank1[345] == bank1[346] && bank1[346] == bank1[347] && bank1[347] == bank1[348]
                    && bank1[348] == bank1[349] && bank1[349] == bank1[350] && bank1[350] == bank1[351] && bank1[351] == bank1[352]
                    && bank1[352] == bank1[353] && bank1[353] == bank1[354] && bank1[354] == bank1[355] && bank1[355] == bank1[356]
                    && bank1[356] == bank1[357] && bank1[357] == bank1[358] && bank1[358] == bank1[359] && bank1[359] == bank1[360]
                    && bank1[360] == bank1[361] && bank1[361] == bank1[362] && bank1[362] == bank1[363] && bank1[363] == bank1[364]
                    && bank1[364] == bank1[365] && bank1[365] == bank1[366] && bank1[366] == bank1[367] && bank1[367] == bank1[368]
                    && bank1[368] == bank1[369] && bank1[369] == bank1[370] && bank1[370] == bank1[371] && bank1[371] == bank1[372]
                    && bank1[372] == bank1[373] && bank1[373] == bank1[374] && bank1[374] == bank1[375] && bank1[375] == bank1[376]
                    && bank1[376] == bank1[377] && bank1[377] == bank1[378] && bank1[378] == bank1[379] && bank1[379] == bank1[380]
                    && bank1[380] == bank1[381] && bank1[381] == bank1[382] && bank1[382] == bank1[383] && bank1[383] == bank1[384]
                    && bank1[384] == bank1[385] && bank1[385] == bank1[386] && bank1[386] == bank1[387] && bank1[387] == bank1[388]
                    && bank1[388] == bank1[389] && bank1[389] == bank1[390] && bank1[390] == bank1[391] && bank1[391] == bank1[392]
                    && bank1[392] == bank1[393] && bank1[393] == bank1[394] && bank1[394] == bank1[395] && bank1[395] == bank1[396]
                    && bank1[396] == bank1[397] && bank1[397] == bank1[398] && bank1[398] == bank1[399] && bank1[399] == bank1[400]
                    && bank1[400] == bank1[401] && bank1[401] == bank1[402] && bank1[402] == bank1[403] && bank1[403] == bank1[404]
                    && bank1[404] == bank1[405] && bank1[405] == bank1[406] && bank1[406] == bank1[407] && bank1[407] == bank1[408]
                    && bank1[408] == bank1[409] && bank1[409] == bank1[410] && bank1[410] == bank1[411] && bank1[411] == bank1[412]
                    && bank1[412] == bank1[413] && bank1[413] == bank1[414] && bank1[414] == bank1[415] && bank1[415] == bank1[416]
                    && bank1[416] == bank1[417] && bank1[417] == bank1[418] && bank1[418] == bank1[419] && bank1[419] == bank1[420]
                    && bank1[420] == bank1[421] && bank1[421] == bank1[422] && bank1[422] == bank1[423] && bank1[423] == bank1[424]
                    && bank1[424] == bank1[425] && bank1[425] == bank1[426] && bank1[426] == bank1[427] && bank1[427] == bank1[428]
                    && bank1[428] == bank1[429] && bank1[429] == bank1[430] && bank1[430] == bank1[431] && bank1[431] == bank1[432]
                    && bank1[432] == bank1[433] && bank1[433] == bank1[434] && bank1[434] == bank1[435] && bank1[435] == bank1[436]
                    && bank1[436] == bank1[437] && bank1[437] == bank1[438] && bank1[438] == bank1[439] && bank1[439] == bank1[440]
                    && bank1[440] == bank1[441] && bank1[441] == bank1[442] && bank1[442] == bank1[443] && bank1[443] == bank1[444]
                    && bank1[444] == bank1[445] && bank1[445] == bank1[446] && bank1[446] == bank1[447] && bank1[447] == bank1[448]
                    && bank1[448] == bank1[449] && bank1[449] == bank1[450] && bank1[450] == bank1[451] && bank1[451] == bank1[452]
                    && bank1[452] == bank1[453] && bank1[453] == bank1[454] && bank1[454] == bank1[455] && bank1[455] == bank1[456]
                    && bank1[456] == bank1[457] && bank1[457] == bank1[458] && bank1[458] == bank1[459] && bank1[459] == bank1[460]
                    && bank1[460] == bank1[461] && bank1[461] == bank1[462] && bank1[462] == bank1[463] && bank1[463] == bank1[464]
                    && bank1[464] == bank1[465] && bank1[465] == bank1[466] && bank1[466] == bank1[467] && bank1[467] == bank1[468]
                    && bank1[468] == bank1[469] && bank1[469] == bank1[470] && bank1[470] == bank1[471] && bank1[471] == bank1[472]
                    && bank1[472] == bank1[473] && bank1[473] == bank1[474] && bank1[474] == bank1[475] && bank1[475] == bank1[476]
                    && bank1[476] == bank1[477] && bank1[477] == bank1[478] && bank1[478] == bank1[479] && bank1[479] == bank1[480]
                    && bank1[480] == bank1[481] && bank1[481] == bank1[482] && bank1[482] == bank1[483] && bank1[483] == bank1[484]
                    && bank1[484] == bank1[485] && bank1[485] == bank1[486] && bank1[486] == bank1[487] && bank1[487] == bank1[488]
                    && bank1[488] == bank1[489] && bank1[489] == bank1[490] && bank1[490] == bank1[491] && bank1[491] == bank1[492]
                    && bank1[492] == bank1[493] && bank1[493] == bank1[494] && bank1[494] == bank1[495] && bank1[495] == bank1[496]
                    && bank1[496] == bank1[497] && bank1[497] == bank1[498] && bank1[498] == bank1[499] && bank1[499] == bank1[500]
                    && bank1[500] == bank1[501] && bank1[501] == bank1[502] && bank1[502] == bank1[503] && bank1[503] == bank1[504]
                    && bank1[504] == bank1[505] && bank1[505] == bank1[506] && bank1[506] == bank1[507] && bank1[507] == bank1[508]
                    && bank1[508] == bank1[509] && bank1[509] == bank1[510] && bank1[510] == bank1[511] && bank1[511] == 0);
                """);
    }

    @Test
    void compilesRealLifeTest1() {
        assertCompilesTo("""
                        n = 0;
                        while (reactor = getlink(n)) != null do
                            if reactor.@liquidCapacity > 0 then
                                pct_avail = reactor.@cryofluid / reactor.@liquidCapacity;
                                reactor.enabled = pct_avail >= 0.25;
                            end;
                            n += 1;
                        end;
                        """,
                createInstruction(SET, "n", "0"),
                createInstruction(LABEL, var(1000)),
                createInstruction(GETLINK, var(0), "n"),
                createInstruction(SET, "reactor", var(0)),
                createInstruction(OP, "notEqual", var(1), "reactor", "null"),
                createInstruction(JUMP, var(1002), "equal", var(1), "false"),
                createInstruction(SENSOR, var(2), "reactor", "@liquidCapacity"),
                createInstruction(OP, "greaterThan", var(3), var(2), "0"),
                createInstruction(JUMP, var(1003), "equal", var(3), "false"),
                createInstruction(SENSOR, var(5), "reactor", "@cryofluid"),
                createInstruction(SENSOR, var(6), "reactor", "@liquidCapacity"),
                createInstruction(OP, "div", var(7), var(5), var(6)),
                createInstruction(SET, "pct_avail", var(7)),
                createInstruction(SET, var(8), "reactor"),
                createInstruction(OP, "greaterThanEq", var(10), "pct_avail", "0.25"),
                createInstruction(CONTROL, "enabled", var(8), var(10)),
                createInstruction(SET, var(4), var(10)),
                createInstruction(JUMP, var(1004), "always"),
                createInstruction(LABEL, var(1003)),
                createInstruction(SET, var(4), "null"),
                createInstruction(LABEL, var(1004)),
                createInstruction(OP, "add", "n", "n", "1"),
                createInstruction(LABEL, var(1001)),
                createInstruction(JUMP, var(1000), "always"),
                createInstruction(LABEL, var(1002))
        );
    }

    @Nested
    class RemoteModulesTest {
        @Test
        void compilesModuleWithRemoteFunctions() {
            assertCompilesTo("""
                            module test;
                            
                            remote void foo(in a, out b)
                                b = 2 * a;
                            end;
                            
                            remote def bar(in x)
                                sin(x) * 2;
                            end;
                            """,
                    createInstruction(JUMP, label(2), "always"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(2)),
                    createInstruction(SET, "*signature", q("8275dc9ca6c8f234:v1")),
                    createInstruction(LABEL, label(3)),
                    createInstruction(WAIT, "1e12"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "mul", tmp(0), "2", ":foo:a"),
                    createInstruction(SET, ":foo:b", tmp(0)),
                    createInstruction(LABEL, label(4)),
                    createInstruction(SET, ":foo*finished", "true"),
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "sin", tmp(1), ":bar:x"),
                    createInstruction(OP, "mul", tmp(2), tmp(1), "2"),
                    createInstruction(SET, ":bar*retval", tmp(2)),
                    createInstruction(LABEL, label(5)),
                    createInstruction(SET, ":bar*finished", "true"),
                    createInstruction(JUMP, label(3), "always")
            );
        }

        @Test
        void compilesModuleWithRemoteFunctionsAndBackgroundProcess() {
            assertCompilesTo("""
                            module test;
                            
                            var invocations = -1;
                            
                            remote void foo(in a, out b)
                                b = 2 * a;
                            end;
                            
                            remote def bar(in x)
                                sin(x) * 2;
                            end;
                            
                            void backgroundProcess()
                                print($"Number of invocations: ${++invocations}");
                                printflush(message1);
                            end;
                            """,
                    createInstruction(JUMP, label(3), "always"),
                    createInstruction(JUMP, label(1), "always"),
                    createInstruction(JUMP, label(0), "always"),
                    createInstruction(LABEL, label(3)),
                    createInstruction(SET, ".invocations", "-1"),
                    createInstruction(SET, "*signature", q("8275dc9ca6c8f234:v1")),
                    createInstruction(LABEL, label(4)),
                    createInstruction(OP, "add", ".invocations", ".invocations", "1"),
                    createInstruction(PRINT, q("Number of invocations: ")),
                    createInstruction(PRINT, ".invocations"),
                    createInstruction(PRINTFLUSH, "message1"),
                    createInstruction(LABEL, label(5)),
                    createInstruction(WAIT, "1e12"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(0)),
                    createInstruction(OP, "mul", tmp(0), "2", ":foo:a"),
                    createInstruction(SET, ":foo:b", tmp(0)),
                    createInstruction(LABEL, label(6)),
                    createInstruction(SET, ":foo*finished", "true"),
                    createInstruction(JUMP, label(4), "always"),
                    createInstruction(END),
                    createInstruction(LABEL, label(1)),
                    createInstruction(OP, "sin", tmp(1), ":bar:x"),
                    createInstruction(OP, "mul", tmp(2), tmp(1), "2"),
                    createInstruction(SET, ":bar*retval", tmp(2)),
                    createInstruction(LABEL, label(7)),
                    createInstruction(SET, ":bar*finished", "true"),
                    createInstruction(JUMP, label(4), "always")
            );
        }
    }

    @Nested
    class TargetGuardsTest {
        @Test
        void compilesGuardForTarget6Compatible() {
            assertCompilesTo("""
                            #set target-guard = true;
                            #set target = 6;
                            
                            print("Hello");
                            """,
                    createInstruction(LABEL, label(0)),
                    createInstruction(PRINT, q("Hello"))
            );
        }

        @Test
        void compilesGuardForTarget6Specific() {
            assertCompilesTo("""
                            #set target-guard = true;
                            #set builtin-evaluation = full;
                            #set target = 6;
                            
                            print("Hello");
                            """,
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(0), "greaterThan", "%FFFFFF", "0"),
                    createInstruction(PRINT, q("Hello"))
            );
        }

        @Test
        void compilesGuardForTarget7Compatible() {
            assertCompilesTo("""
                            #set target-guard = true;
                            #set target = 7;
                            
                            print("Hello");
                            """,
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(0), "strictEqual", "%FFFFFF", "null"),
                    createInstruction(PRINT, q("Hello"))
            );
        }

        @Test
        void compilesGuardForTarget7Specific() {
            assertCompilesTo("""
                            #set target-guard = true;
                            #set builtin-evaluation = full;
                            #set target = 7;
                            
                            print("Hello");
                            """,
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(0), "notEqual", "@blockCount", "254"),
                    createInstruction(PRINT, q("Hello"))
            );
        }

        @Test
        void compilesGuardForTarget8Compatible() {
            assertCompilesTo("""
                            #set target-guard = true;
                            #set target = 8;
                            
                            print("Hello");
                            """,
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(0), "strictEqual", "%[red]", "null"),
                    createInstruction(PRINT, q("Hello"))
            );
        }

        @Test
        void compilesGuardForTarget8Specific() {
            assertCompilesTo("""
                            #set target-guard = true;
                            #set builtin-evaluation = full;
                            #set target = 8;
                            
                            print("Hello");
                            """,
                    createInstruction(LABEL, label(0)),
                    createInstruction(JUMP, label(0), "strictEqual", "%[red]", "null"),
                    createInstruction(PRINT, q("Hello"))
            );
        }
    }
}
