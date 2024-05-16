import * as echarts from 'echarts/core';

// Import bar charts, all suffixed with Chart
import {BarChart, LineChart, PieChart, TreemapChart} from 'echarts/charts';

// Import the tooltip, title, rectangular coordinate system, dataset and transform components
import {DatasetComponent, GridComponent, LegendComponent, TitleComponent, TooltipComponent, TransformComponent} from 'echarts/components';

// Features like Universal Transition and Label Layout
import {LabelLayout, UniversalTransition} from 'echarts/features';

// Import the Canvas renderer
// Note that including the CanvasRenderer or SVGRenderer is a required step
import {SVGRenderer} from 'echarts/renderers';

// Register the required components
echarts.use([
    BarChart,
    LineChart,
    PieChart,
    TreemapChart,
    TitleComponent,
    TooltipComponent,
    GridComponent,
    DatasetComponent,
    TransformComponent,
    LegendComponent,
    LabelLayout,
    UniversalTransition,
    SVGRenderer
]);
export default echarts;
