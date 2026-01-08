import React from 'react';
import {Asset, Result} from "@toss/tds-react-native";

export default function NotFoundPage() {
    return (
        <Result
            figure={<Asset.Icon name="icn-info-line" frameShape={Asset.frameShape.CleanH24}/>}
            title="다시 시도해주세요"
            description={`시스템에 잠깐 문제가 생겨 화면을 불러오지 못했어요.`}
        />
    );
}
