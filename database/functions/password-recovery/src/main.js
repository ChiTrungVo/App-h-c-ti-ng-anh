function page(title, message, deepLink, status = 200) {
  return {
    status,
    html: `<!doctype html>
<html lang="vi">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>${title}</title>
    <style>
      body {
        margin: 0;
        min-height: 100vh;
        display: grid;
        place-items: center;
        background: #f7fbff;
        color: #06263a;
        font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      }
      main {
        width: min(90vw, 440px);
        padding: 32px;
        border: 2px solid #78d6f0;
        border-radius: 28px;
        background: white;
        text-align: center;
        box-shadow: 0 20px 45px rgb(0 86 120 / 14%);
      }
      h1 { margin: 0 0 12px; font-size: 30px; }
      p { margin: 0 0 24px; font-size: 17px; line-height: 1.5; color: #4c5962; }
      a {
        display: inline-block;
        padding: 14px 22px;
        border-radius: 999px;
        background: #087b92;
        color: white;
        font-weight: 700;
        text-decoration: none;
      }
    </style>
  </head>
  <body>
    <main>
      <h1>${title}</h1>
      <p>${message}</p>
      ${deepLink ? `<a href="${deepLink}">Mở MinLish</a>` : ''}
    </main>
  </body>
</html>`
  };
}

export default async ({ req, res }) => {
  const url = new URL(req.url || '/', 'https://minlish.local');
  const userId = req.query?.userId || url.searchParams.get('userId');
  const secret = req.query?.secret || url.searchParams.get('secret');

  if (!userId || !secret) {
    const body = page(
      'Liên kết không hợp lệ',
      'Liên kết đặt lại mật khẩu thiếu thông tin userId hoặc secret.',
      null,
      400
    );
    return res.send(body.html, body.status, { 'content-type': 'text/html; charset=utf-8' });
  }

  const deepLink = `minlish://reset-password?userId=${encodeURIComponent(userId)}&secret=${encodeURIComponent(secret)}`;
  const body = page(
    'Đặt lại mật khẩu',
    'Bấm nút bên dưới để mở MinLish và nhập mật khẩu mới.',
    deepLink
  );
  return res.send(body.html, body.status, { 'content-type': 'text/html; charset=utf-8' });
};
