package com.m2e.dashboard.dto.overview.response;

import com.m2e.dashboard.enums.OvSubProductEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class OverviewSubResDTOTest {
    /**    Correct Order per in UST docx
     *
     *     HP("HP","Hire Purchase"),
     *     BHP("BHP","Blanket/Business Hire Purchase"),
     *     FS("FS","Floor Stock"),
     *     BD("BD","Block Discounting"),
     *     PBR("PBR","Payment Before Registration"),
     */
    @Test
    void test_sequence_order_of_autofinace() {
        //Given
        OverviewSubResDTO bd = new OverviewSubResDTO();
        bd.setSubProduct(OvSubProductEnum.BD);

        OverviewSubResDTO fs = new OverviewSubResDTO();
        fs.setSubProduct(OvSubProductEnum.FS);

        OverviewSubResDTO hp = new OverviewSubResDTO();
        hp.setSubProduct(OvSubProductEnum.HP);

        OverviewSubResDTO pbr = new OverviewSubResDTO();
        pbr.setSubProduct(OvSubProductEnum.PBR);

        OverviewSubResDTO fca = new OverviewSubResDTO();
        fca.setSubProduct(OvSubProductEnum.FCA);


        final List<OverviewSubResDTO> unsortedList = new ArrayList<>() {{
            add(fca); //Added Non-Autofinance to simulate case where non-autofinance item is added, then push this to the lowest position


            add(bd);
            add(fs);
            add(hp);
            add(pbr);
        }};


        //When get-sorted
        List<OverviewSubResDTO> sortedList = unsortedList.stream().sorted().toList();
        //Then verify position
        assertEquals(OvSubProductEnum.HP, sortedList.get(0).getSubProduct());
        assertEquals(OvSubProductEnum.FS, sortedList.get(1).getSubProduct());
        assertEquals(OvSubProductEnum.BD, sortedList.get(2).getSubProduct());
        assertEquals(OvSubProductEnum.PBR, sortedList.get(3).getSubProduct());
    }
}