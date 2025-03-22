import { config } from "../libs/config";
import { constant } from "../libs/constant";
import { FooterResponse } from "../types/service";

export async function FooterData(): Promise<FooterResponse> {

  const API_URL = (constant.FOOTER_DATA && typeof constant.FOOTER_DATA === 'string') 
    ? constant.FOOTER_DATA 
    : `${config.API_BASE_URL}/bbs`;

  const response = await fetch(API_URL, { next: { revalidate: false } });
  
  if (!response.ok) {
    throw new Error(`Failed to fetch: ${response.statusText}`);
  }
  
  return response.json();
}