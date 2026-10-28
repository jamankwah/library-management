package com.library.loader;

import com.library.exception.InvalidDataException;
import com.library.model.Member;
import com.library.model.MembershipType;

/** Format: id,name,membershipType,outstandingFees */
public class MemberCsvLoader extends CsvLoader<Member> {

    @Override
    protected Member parseLine(String[] fields) throws InvalidDataException {
        checkFieldCount(fields, 4);
        MembershipType type = parseMembershipType(fields[2]);
        double fees = parseDouble(fields[3], "outstandingFees");
        return new Member(fields[0].trim(), fields[1].trim(), type, fees);
    }

    private MembershipType parseMembershipType(String value) throws InvalidDataException {
        try {
            return MembershipType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException("unknown membership type '" + value + "'");
        }
    }
}