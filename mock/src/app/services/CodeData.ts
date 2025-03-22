import { config } from "../libs/config";
import { constant } from "../libs/constant";
import { CodeDataResponse } from "../types/service";

export async function CodeData(): Promise<any> { 
  const API_URL = (constant.CODE_DATA && typeof constant.CODE_DATA === 'string') 
    ? constant.CODE_DATA 
    : `${config.API_BASE_URL}/bbs`;

    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error(`Failed to fetch code data: ${response.statusText}`);
    }
    const data: CodeDataResponse = await response.json();
    return data.value;
  }
  