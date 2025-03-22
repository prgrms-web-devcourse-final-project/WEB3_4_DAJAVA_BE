import { hasLocale} from "next-intl";
import { routing } from "../../i18n/routing";
import { notFound } from "next/navigation";
import { RootLayoutProps } from "../types/layout";
import ProgressBar from "../components/common/progressBar/ProgressBar";
import SkipNavigation from "../components/layout/skipNavigation/SkipNavigation";
import "./styles/globals.css";


async function RootLayout({ children, params }: RootLayoutProps) {
  const {locale} = await params;
  if (!hasLocale(routing.locales, locale)) {
    notFound();
  }

  return (
      <html lang={locale}>
        {/* <CodeContextProvider> */}
          <body>
          <SkipNavigation locale={locale} />
          <ProgressBar />
              {children}
          </body>
        {/* </CodeContextProvider> */}
      </html>
  );
}

export default RootLayout;
