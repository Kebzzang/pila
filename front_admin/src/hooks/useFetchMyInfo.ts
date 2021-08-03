import { useEffect, useState } from 'react';
import { IClasses } from '../types/db';
import axios from 'axios';

export default function useFetchMyInfo(userId: number, cancel: boolean) {
  const [data, setData] = useState<IClasses>({ count: 0, data: [] });
  useEffect(() => {
    axios
      .get(`http://ec2-3-38-35-210.ap-northeast-2.compute.amazonaws.com:8080/api/v1/course/me/${userId}`, {
        withCredentials: true,
      })
      .then((r) => {
        setData(r.data);
      });
  }, [cancel, userId]);
  return data;
}
